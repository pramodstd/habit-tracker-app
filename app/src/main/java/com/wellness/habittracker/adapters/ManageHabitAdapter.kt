package com.wellness.habittracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wellness.habittracker.R
import com.wellness.habittracker.models.Habit

class ManageHabitAdapter(
    private val items: List<Habit>,
    private val onEdit: (Habit) -> Unit,
    private val onDelete: (Habit) -> Unit
) : RecyclerView.Adapter<ManageHabitAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tvIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_manage_habit, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val h = items[position]
        holder.tvIcon.text = h.icon
        holder.tvTitle.text = h.name
        holder.tvSubtitle.text = "${h.category} • Goal: ${h.goal} ${h.unit}"
        holder.btnEdit.setOnClickListener { onEdit(h) }
        holder.btnDelete.setOnClickListener { onDelete(h) }
        holder.itemView.setOnClickListener { onEdit(h) }
    }

    override fun getItemCount() = items.size
}