package com.wellness.habittracker.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wellness.habittracker.utils.HydrationScheduler
import com.wellness.habittracker.utils.NotificationUtils
import com.wellness.habittracker.utils.PreferencesHelper

class HydrationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = PreferencesHelper(context)
        // Stop if user turned off reminders
        if (!prefs.hydrationReminder) return

        // Show the notification
        NotificationUtils.showHydrationNotification(context)

        // Reschedule the next reminder (interval read from prefs)
        HydrationScheduler.scheduleNext(context)
    }
}