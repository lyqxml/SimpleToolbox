package com.lyq.simpletoolbox

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToolAdapter(private val tools: List<Tool>) : RecyclerView.Adapter<ToolAdapter.ToolHolder>() {

    class ToolHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.toolIcon)
        val name: TextView = view.findViewById(R.id.toolName)
        val desc: TextView = view.findViewById(R.id.toolDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tool, parent, false)
        return ToolHolder(view)
    }

    override fun getItemCount(): Int = tools.size

    override fun onBindViewHolder(holder: ToolHolder, position: Int) {
        val tool = tools[position]
        val context = holder.itemView.context
        holder.icon.setImageResource(tool.iconRes)
        holder.name.text = context.getString(tool.nameRes)
        holder.desc.text = context.getString(tool.descRes)
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, tool.target))
        }
    }
}
