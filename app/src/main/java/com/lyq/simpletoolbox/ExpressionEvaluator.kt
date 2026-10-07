package com.lyq.simpletoolbox

import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.abs

/**
 * 极简四则运算求值器（递归下降实现）。
 * 支持 + - * / % ( ) 以及小数、正负号。
 */
object ExpressionEvaluator {

    fun evaluate(raw: String): Double {
        val expr = raw
            .replace('×', '*')
            .replace('÷', '/')
            .replace('−', '-')
            .replace('（', '(')
            .replace('）', ')')
        val parser = Parser(expr)
        val value = parser.parseExpression()
        parser.skipSpaces()
        check(parser.isEnd()) { "表达式有误" }
        check(!value.isNaN() && !value.isInfinite()) { "结果无效" }
        return value
    }

    /** 整数不显示小数点，小数最多保留 10 位有效数字并去掉末尾多余的 0 */
    fun format(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "错误"
        if (abs(value) < 1e15 && value == value.toLong().toDouble()) {
            return value.toLong().toString()
        }
        return BigDecimal(value)
            .round(MathContext(10))
            .stripTrailingZeros()
            .toPlainString()
    }

    private class Parser(private val s: String) {
        private var index = 0

        fun isEnd(): Boolean = index >= s.length

        fun skipSpaces() {
            while (index < s.length && s[index] == ' ') index++
        }

        private fun peek(): Char? {
            skipSpaces()
            return if (index < s.length) s[index] else null
        }

        fun parseExpression(): Double {
            var value = parseTerm()
            while (true) {
                val c = peek() ?: break
                if (c == '+' || c == '-') {
                    index++
                    val right = parseTerm()
                    value = if (c == '+') value + right else value - right
                } else {
                    break
                }
            }
            return value
        }

        private fun parseTerm(): Double {
            var value = parseUnary()
            while (true) {
                val c = peek() ?: break
                if (c == '*' || c == '/' || c == '%') {
                    index++
                    val right = parseUnary()
                    value = when (c) {
                        '*' -> value * right
                        '/' -> value / right
                        else -> value % right
                    }
                } else {
                    break
                }
            }
            return value
        }

        private fun parseUnary(): Double {
            val c = peek()
            if (c == '-') {
                index++
                return -parseUnary()
            }
            if (c == '+') {
                index++
                return parseUnary()
            }
            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            when (peek()) {
                '(' -> {
                    index++
                    val value = parseExpression()
                    check(peek() == ')') { "括号不匹配" }
                    index++
                    return value
                }
                null -> throw IllegalArgumentException("表达式不完整")
            }

            val start = index
            while (index < s.length && (s[index].isDigit() || s[index] == '.')) index++
            check(index > start) { "表达式有误" }
            return s.substring(start, index).toDouble()
        }
    }
}
