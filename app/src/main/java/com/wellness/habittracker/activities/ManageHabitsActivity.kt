package com.wellness.habittracker.activities

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wellness.habittracker.R
import com.wellness.habittracker.adapters.ManageHabitAdapter
import com.wellness.habittracker.models.Habit
import com.wellness.habittracker.utils.PreferencesHelper
import java.util.UUID

class ManageHabitsActivity : AppCompatActivity() {

    private lateinit var prefs: PreferencesHelper
    private lateinit var recycler: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: ManageHabitAdapter
    private var habits: MutableList<Habit> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_habits)
        
        // FIX: Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_green_dark)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = 0
        }

        supportActionBar?.title = getString(R.string.manage_habits_title)

        prefs = PreferencesHelper(this)
        recycler = findViewById(R.id.recyclerManageHabits)
        emptyText = findViewById(R.id.tvEmptyManage)

        habits = prefs.getHabits()

        adapter = ManageHabitAdapter(habits,
            onEdit = { habit -> showAddEditDialog(habit) },
            onDelete = { habit ->
                habits.removeIf { it.id == habit.id }
                prefs.saveHabits(habits)
                refresh()
            }
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val fab: FloatingActionButton = findViewById(R.id.fabAddHabit)
        fab.setOnClickListener { showAddEditDialog(null) }

        refresh()
    }

    private fun refresh() {
        adapter.notifyDataSetChanged()
        emptyText.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showAddEditDialog(editing: Habit?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_habit, null, false)

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

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (editing == null) R.string.add_habit else R.string.edit_habit)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, null) // we override later
            .create()

        dialog.setOnShowListener {
            val okBtn: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okBtn.setOnClickListener {
                val name = etName.text.toString().trim()
                val category = etCategory.text.toString().trim()
                val icon = etIcon.text.toString().ifBlank { "✅" }
                val goalText = etGoal.text.toString().trim()
                val unit = etUnit.text.toString().ifBlank { "times" }

                if (name.isEmpty() || goalText.isEmpty()) {
                    // simple validation
                    etName.error = if (name.isEmpty()) getString(R.string.required) else null
                    etGoal.error = if (goalText.isEmpty()) getString(R.string.required) else null
                    return@setOnClickListener
                }

                val goal = goalText.toIntOrNull() ?: 1

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

                prefs.saveHabits(habits)
                refresh()
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}