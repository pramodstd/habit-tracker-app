package com.wellness.habittracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wellness.habittracker.R
import com.wellness.habittracker.adapters.HabitAdapter
import com.wellness.habittracker.models.Habit
import com.wellness.habittracker.utils.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class HabitsFragment : Fragment() {

    private lateinit var prefsHelper: PreferencesHelper
    private lateinit var habitAdapter: HabitAdapter

    private lateinit var tvProgressPercent: TextView
    private lateinit var tvCompletedHabits: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvDate: TextView

    // Make habits accessible to dialog/edit/delete
    private val habits = mutableListOf<Habit>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_habits, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsHelper = PreferencesHelper(requireContext())

        tvProgressPercent = view.findViewById(R.id.tvProgressPercent)
        tvCompletedHabits = view.findViewById(R.id.tvCompletedHabits)
        progressBar = view.findViewById(R.id.progressBar)
        tvDate = view.findViewById(R.id.tvDate)

        val fab: View = view.findViewById(R.id.fabAddHabitMain)
        // FIX: call the actual function name you defined below
        fab.setOnClickListener { showAddEditDialog(null) }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHabits)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set current date
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        tvDate.text = dateFormat.format(Date())

        // Load and bind
        loadHabits()
        habitAdapter = HabitAdapter(habits) { habit, action ->
            when (action) {
                "increment" -> {
                    habit.incrementProgress()
                    prefsHelper.saveHabits(habits)
                    updateProgress(habits)
                    habitAdapter.notifyDataSetChanged()
                }
                "decrement" -> {
                    habit.decrementProgress()
                    prefsHelper.saveHabits(habits)
                    updateProgress(habits)
                    habitAdapter.notifyDataSetChanged()
                }
                "edit" -> {
                    showAddEditDialog(habit)
                }
                "delete" -> {
                    val removed = habits.removeIf { it.id == habit.id }
                    if (removed) {
                        prefsHelper.saveHabits(habits)
                        updateProgress(habits)
                        habitAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        recyclerView.adapter = habitAdapter
        updateProgress(habits)
    }

    private fun loadHabits() {
        habits.clear()
        habits.addAll(prefsHelper.getHabits())

        // If you have hydration settings to sync into a "Hydration" habit,
        // re-enable with your actual API names on PreferencesHelper, e.g.:
        //
        // val idx = habits.indexOfFirst { it.name.equals("Hydration", ignoreCase = true) }
        // if (idx >= 0) {
        //     habits[idx].goal = prefsHelper.hydrationGoal
        //     habits[idx].currentProgress = prefsHelper.getHydrationCountToday()
        //     habits[idx].isCompleted = habits[idx].currentProgress >= habits[idx].goal
        // }
    }

    private fun updateProgress(habits: List<Habit>) {
        val totalHabits = habits.size
        val completedHabits = habits.count { it.isCompleted }
        val progressPercent = if (totalHabits > 0) (completedHabits * 100) / totalHabits else 0

        tvProgressPercent.text = "$progressPercent%"
        tvCompletedHabits.text = "$completedHabits of $totalHabits habits completed"
        progressBar.progress = progressPercent
    }

    override fun onResume() {
        super.onResume()
        loadHabits()
        if (this::habitAdapter.isInitialized) {
            habitAdapter.notifyDataSetChanged()
        }
        updateProgress(habits)
    }

    private fun showAddEditDialog(editing: Habit?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_edit_habit, null, false)

        val etName = dialogView.findViewById<EditText>(R.id.etHabitName)
        val etCategory = dialogView.findViewById<EditText>(R.id.etHabitCategory)
        val etIcon = dialogView.findViewById<EditText>(R.id.etHabitIcon)
        val etGoal = dialogView.findViewById<EditText>(R.id.etHabitGoal)
        val etUnit = dialogView.findViewById<EditText>(R.id.etHabitUnit)

        if (editing != null) {
            etName.setText(editing.name)
            etCategory.setText(editing.category)
            etIcon.setText(editing.icon)
            etGoal.setText(editing.goal.toString())
            etUnit.setText(editing.unit)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (editing == null) R.string.add_habit else R.string.edit_habit)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, null)
            .create()

        dialog.setOnShowListener {
            val okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okBtn.setOnClickListener {
                val name = etName.text.toString().trim()
                val category = etCategory.text.toString().trim()
                val icon = etIcon.text.toString().ifBlank { "✅" }
                val goal = etGoal.text.toString().toIntOrNull() ?: 1
                val unit = etUnit.text.toString().ifBlank { "times" }

                if (name.isEmpty()) {
                    etName.error = getString(R.string.required)
                    return@setOnClickListener
                }

                if (editing == null) {
                    val newHabit = Habit(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        category = category,
                        icon = icon,
                        goal = goal,
                        unit = unit
                    )
                    habits.add(newHabit)
                } else {
                    editing.name = name
                    editing.category = category
                    editing.icon = icon
                    editing.goal = goal
                    editing.unit = unit
                }

                prefsHelper.saveHabits(habits)
                if (this::habitAdapter.isInitialized) habitAdapter.notifyDataSetChanged()
                updateProgress(habits)
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}