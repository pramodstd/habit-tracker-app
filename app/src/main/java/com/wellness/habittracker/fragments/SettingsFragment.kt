package com.wellness.habittracker.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.wellness.habittracker.R
import com.wellness.habittracker.reminders.HydrationReminderReceiver
import com.wellness.habittracker.utils.HydrationScheduler
import com.wellness.habittracker.utils.PreferencesHelper
import com.wellness.habittracker.widget.TodayWidgetProvider
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SettingsFragment : Fragment() {

    private lateinit var prefs: PreferencesHelper

    private lateinit var tvHydrationProgress: TextView
    private lateinit var tvHydrationGoal: TextView
    private lateinit var seekBarHydrationGoal: SeekBar
    private lateinit var btnInc: TextView
    private lateinit var btnDec: TextView

    private lateinit var switchHydrationReminder: SwitchCompat
    private lateinit var spinnerHydrationInterval: Spinner

    private lateinit var switchGoalDeadlines: SwitchCompat
    private lateinit var switchWeeklyReports: SwitchCompat
    private lateinit var switchMotivationalQuotes: SwitchCompat
    private lateinit var switchSleepReminder: SwitchCompat

    private lateinit var btnSave: Button

    // Must match the requestCode used when scheduling
    private val reminderRequestCode = 1002

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PreferencesHelper(requireContext())

        tvHydrationProgress = view.findViewById(R.id.tvHydrationProgress)
        tvHydrationGoal = view.findViewById(R.id.tvHydrationGoal)
        seekBarHydrationGoal = view.findViewById(R.id.seekBarHydrationGoal)
        btnInc = view.findViewById(R.id.btnIncrementWater)
        btnDec = view.findViewById(R.id.btnDecrementWater)

        switchHydrationReminder = view.findViewById(R.id.switchHydrationReminder)
        spinnerHydrationInterval = view.findViewById(R.id.spinnerHydrationInterval)

        switchGoalDeadlines = view.findViewById(R.id.switchGoalDeadlines)
        switchWeeklyReports = view.findViewById(R.id.switchWeeklyReports)
        switchMotivationalQuotes = view.findViewById(R.id.switchMotivationalQuotes)
        switchSleepReminder = view.findViewById(R.id.switchSleepReminder)

        btnSave = view.findViewById(R.id.btnSaveSettings)

        // Spinner options
        val options = listOf("10 seconds", "10 minutes", "1 hour")
        spinnerHydrationInterval.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            options
        )

        // Init UI from prefs
        seekBarHydrationGoal.progress = prefs.hydrationGoal
        tvHydrationGoal.text = getString(R.string.glasses_text, prefs.hydrationGoal)
        switchHydrationReminder.isChecked = prefs.hydrationReminder
        switchGoalDeadlines.isChecked = prefs.goalDeadlines
        switchWeeklyReports.isChecked = prefs.weeklyReports
        switchMotivationalQuotes.isChecked = prefs.motivationalQuotes
        switchSleepReminder.isChecked = prefs.sleepReminder

        spinnerHydrationInterval.setSelection(
            when (prefs.hydrationReminderIntervalSec) {
                10 -> 0
                600 -> 1
                3600 -> 2
                else -> 1
            }
        )

        updateHydrationUi()

        // Goal slider
        seekBarHydrationGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val goal = max(1, progress)
                prefs.hydrationGoal = goal
                tvHydrationGoal.text = getString(R.string.glasses_text, goal)
                updateHydrationUi()
                TodayWidgetProvider.updateAll(requireContext())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Inc/Dec water count
        btnInc.setOnClickListener {
            val goal = max(1, prefs.hydrationGoal)
            prefs.hydrationCount = min(goal, prefs.hydrationCount + 1)
            updateHydrationUi()
            TodayWidgetProvider.updateAll(requireContext())
        }
        btnDec.setOnClickListener {
            prefs.hydrationCount = max(0, prefs.hydrationCount - 1)
            updateHydrationUi()
            TodayWidgetProvider.updateAll(requireContext())
        }

        // Toggle reminders immediately
        switchHydrationReminder.setOnCheckedChangeListener { _, checked ->
            prefs.hydrationReminder = checked
            // Save the interval BEFORE scheduling
            prefs.hydrationReminderIntervalSec = selectedIntervalSec()

            if (checked) {
                // Prompt for exact alarms if missing (Android 12+)
                val am = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Allow exact alarms")
                        .setMessage("To deliver reminders exactly on time, allow exact alarms for Wellness Tracker.")
                        .setPositiveButton("Open Settings") { _, _ ->
                            val i = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                        }
                        .setNegativeButton("Not now", null)
                        .show()
                }

                HydrationScheduler.scheduleNext(requireContext())
                Toast.makeText(requireContext(), "Hydration reminders ON (every ${getIntervalText()})", Toast.LENGTH_SHORT).show()
            } else {
                cancelHydrationAlarms()
                Toast.makeText(requireContext(), "Hydration reminders OFF", Toast.LENGTH_SHORT).show()
            }
        }

        // Save everything
        btnSave.setOnClickListener {
            prefs.hydrationReminder = switchHydrationReminder.isChecked
            prefs.goalDeadlines = switchGoalDeadlines.isChecked
            prefs.weeklyReports = switchWeeklyReports.isChecked
            prefs.motivationalQuotes = switchMotivationalQuotes.isChecked
            prefs.sleepReminder = switchSleepReminder.isChecked
            prefs.hydrationReminderIntervalSec = selectedIntervalSec()

            if (prefs.hydrationReminder) {
                cancelHydrationAlarms()

                // Prompt for exact alarms if missing (Android 12+)
                val am = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Allow exact alarms")
                        .setMessage("To deliver reminders exactly on time, allow exact alarms for Wellness Tracker.")
                        .setPositiveButton("Open Settings") { _, _ ->
                            val i = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                        }
                        .setNegativeButton("Not now", null)
                        .show()
                }

                HydrationScheduler.scheduleNext(requireContext())
            } else {
                cancelHydrationAlarms()
            }

            Toast.makeText(requireContext(), getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateHydrationUi() {
        val goal = max(1, prefs.hydrationGoal)
        val count = min(prefs.hydrationCount, goal)
        val pct = ((count.toFloat() / goal.toFloat()) * 100f).roundToInt()
        tvHydrationProgress.text = getString(R.string.hydration_progress_template, count, goal, pct)
    }

    private fun selectedIntervalSec(): Int = when (spinnerHydrationInterval.selectedItemPosition) {
        0 -> 10
        1 -> 600
        2 -> 3600
        else -> 600
    }

    private fun getIntervalText(): String = when (spinnerHydrationInterval.selectedItemPosition) {
        0 -> "10 seconds"
        1 -> "10 minutes"
        2 -> "1 hour"
        else -> "10 minutes"
    }

    // ---- Scheduling helpers (no external dependency) ----

    private fun cancelHydrationAlarms() {
        val am = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), HydrationReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            requireContext(),
            reminderRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pi)
    }
}