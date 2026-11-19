package com.wellness.habittracker.boot

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.wellness.habittracker.utils.HydrationScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Boot completed, attempting to reschedule hydration reminders")

            // Check if we have permission to schedule exact alarms
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.w("BootReceiver", "Cannot schedule exact alarms - permission not granted")
                    return
                }
            }

            // Only reschedule if reminders are enabled
            val prefs = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
            val remindersEnabled = prefs.getBoolean("hydration_reminders_enabled", false)

            if (remindersEnabled) {
                HydrationScheduler.scheduleNext(context)
                Log.d("BootReceiver", "Hydration reminders rescheduled after boot")
            } else {
                Log.d("BootReceiver", "Hydration reminders are disabled, not scheduling")
            }
        }
    }
}
