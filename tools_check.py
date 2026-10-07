#!/usr/bin/env python3
"""轻量自检（不需要编译）：校验 XML 合法性 + 资源引用是否齐全。

用法：python3 tools_check.py
说明：Android 框架资源和 Material 组件库里的样式不在检查范围内。
"""
import os
import re
import sys
import xml.etree.ElementTree as ET

ROOT = os.path.dirname(os.path.abspath(__file__))
RES = os.path.join(ROOT, "app", "src", "main", "res")
SRC = os.path.join(ROOT, "app", "src", "main", "java")

# 第三方/系统自带的样式前缀，不做存在性校验
LIBRARY_PREFIXES = (
    "Widget.Material3", "Widget.Material", "Theme.Material3", "Theme.Material",
    "Theme.AppCompat", "ThemeOverlay", "TextAppearance", "Base.",
)

errors = []

xml_files = []
for base, dirs, files in os.walk(ROOT):
    dirs[:] = [d for d in dirs if d not in (".git", "build", ".gradle")]
    for name in files:
        if name.endswith(".xml"):
            xml_files.append(os.path.join(base, name))

for path in sorted(xml_files):
    try:
        ET.parse(path)
    except Exception as exc:
        errors.append("XML 解析失败 %s：%s" % (os.path.relpath(path, ROOT), exc))

defined = {}
for base, _, files in os.walk(RES):
    folder = os.path.basename(base)
    res_type = folder.split("-")[0]
    if folder in ("values", "values-night"):
        for name in files:
            if not name.endswith(".xml"):
                continue
            for child in ET.parse(os.path.join(base, name)).getroot():
                if child.get("name"):
                    defined.setdefault(child.tag, set()).add(child.get("name"))
    else:
        for name in files:
            if name.endswith(".xml"):
                defined.setdefault(res_type, set()).add(name[:-4])

# 布局里 @+id/ 定义的控件 id
id_ref = re.compile(r'@\+id/(\w+)')
for base, _, files in os.walk(RES):
    for name in files:
        if name.endswith(".xml") and os.path.basename(base).startswith("layout"):
            for match in id_ref.finditer(open(os.path.join(base, name), encoding="utf-8").read()):
                defined.setdefault("id", set()).add(match.group(1))

ref = re.compile(r'(?<![\w.])@(?:\+)?([a-z]+)/([\w.]+)')
for path in sorted(xml_files):
    text = open(path, encoding="utf-8").read()
    for match in ref.finditer(text):
        rtype, rname = match.group(1), match.group(2)
        if rtype in ("id", "attr") or rname.startswith(LIBRARY_PREFIXES):
            continue
        if rname in defined.get(rtype, set()):
            continue
        errors.append("缺少资源 @%s/%s（%s）" % (rtype, rname, os.path.relpath(path, ROOT)))

kt_ref = re.compile(r'(?<![\w.])R\.([a-z]+)\.(\w+)')
for base, _, files in os.walk(SRC):
    for name in files:
        if not name.endswith(".kt"):
            continue
        path = os.path.join(base, name)
        for match in kt_ref.finditer(open(path, encoding="utf-8").read()):
            rtype, rname = match.group(1), match.group(2)
            if rname in defined.get(rtype, set()):
                continue
            errors.append("缺少资源 R.%s.%s（%s）" % (rtype, rname, os.path.relpath(path, ROOT)))

print("已校验 %d 个 XML 文件" % len(xml_files))
if errors:
    print("发现 %d 处问题：" % len(errors))
    for item in errors:
        print("  -", item)
    sys.exit(1)
print("资源引用检查通过 ✓")
