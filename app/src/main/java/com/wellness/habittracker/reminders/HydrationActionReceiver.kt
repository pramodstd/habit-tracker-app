package com.wellness.habittracker.reminders

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.wellness.habittracker.utils.PreferencesHelper
import com.wellness.habittracker.widget.TodayWidgetProvider

class HydrationActionReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_INCREMENT = "com.wellness.habittracker.action.INCREMENT_GLASS"
        private const val REQ = 30205
        fun pendingIntent(context: Context, intent: Intent): PendingIntent {
            return PendingIntent.getBroadcast(context, REQ, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_INCREMENT) {
            val prefs = PreferencesHelper(context)
            prefs.hydrationCount = prefs.hydrationCount + 1
            NotificationManagerCompat.from(context).cancel(1001)
            TodayWidgetProvider.updateAll(context)
        }
    }
}
