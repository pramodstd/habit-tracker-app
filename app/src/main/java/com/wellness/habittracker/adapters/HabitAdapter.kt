package com.wellness.habittracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wellness.habittracker.R
import com.wellness.habittracker.models.Habit

class HabitAdapter(
    private val habits: List<Habit>,
    private val onAction: (Habit, String) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHabitIcon: TextView = itemView.findViewById(R.id.tvHabitIcon)
        val tvHabitName: TextView = itemView.findViewById(R.id.tvHabitName)
        val tvHabitCategory: TextView = itemView.findViewById(R.id.tvHabitCategory)
        val tvHabitStreak: TextView = itemView.findViewById(R.id.tvHabitStreak)
        val tvHabitProgress: TextView = itemView.findViewById(R.id.tvHabitProgress)
        val tvHabitPercent: TextView = itemView.findViewById(R.id.tvHabitPercent)
        val progressBar: ProgressBar = itemView.findViewById(R.id.habitProgressBar)
        val btnDecrement: View = itemView.findViewById(R.id.btnDecrement)
        val btnIncrement: View = itemView.findViewById(R.id.btnIncrement)
    
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEditHabit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteHabit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        
        holder.tvHabitIcon.text = habit.icon
        holder.tvHabitName.text = habit.name
        holder.tvHabitCategory.text = habit.category
        holder.tvHabitStreak.text = "🔥 ${habit.streak} day streak"
        holder.tvHabitProgress.text = "${habit.currentProgress} / ${habit.goal} ${habit.unit}"
        holder.tvHabitPercent.text = "${habit.getProgressPercentage()}% complete"
        holder.progressBar.progress = habit.getProgressPercentage()
        
        holder.btnIncrement.setOnClickListener {
            onAction(habit, "increment")
        }
        
        holder.btnDecrement.setOnClickListener {
            onAction(habit, "decrement")
        }
        holder.btnEdit.setOnClickListener { onAction(habit, "edit") }
        holder.btnDelete.setOnClickListener { onAction(habit, "delete") }
    }

    override fun getItemCount() = habits.size
}
