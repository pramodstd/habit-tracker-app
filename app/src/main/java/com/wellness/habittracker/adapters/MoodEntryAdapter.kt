package com.wellness.habittracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wellness.habittracker.R
import com.wellness.habittracker.models.MoodEntry

class MoodEntryAdapter(
    private val entries: MutableList<MoodEntry>,
    private val onEdit: (MoodEntry) -> Unit = {},
    private val onDelete: (MoodEntry) -> Unit = {}
) : RecyclerView.Adapter<MoodEntryAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvMoodEmoji)
        val tvLabel: TextView = view.findViewById(R.id.tvMoodLabel)
        val tvDateTime: TextView = view.findViewById(R.id.tvMoodDateTime)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditMood)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteMood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_mood_entry, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, position: Int) {
        val e = entries[position]
        h.tvEmoji.text = e.emoji()
        h.tvLabel.text = e.label()
        h.tvDateTime.text = "${e.date}  •  ${e.time}"
        h.btnEdit.setOnClickListener { onEdit(e) }
        h.btnDelete.setOnClickListener { onDelete(e) }
    }

    override fun getItemCount() = entries.size

    fun setData(newList: List<MoodEntry>) {
        entries.clear()
        entries.addAll(newList)
        notifyDataSetChanged()
    }
}