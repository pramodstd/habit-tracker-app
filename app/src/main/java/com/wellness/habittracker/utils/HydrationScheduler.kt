package com.wellness.habittracker.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.wellness.habittracker.reminders.HydrationReminderReceiver
import kotlin.math.max

object HydrationScheduler {

    private const val TAG = "HydrationScheduler"
    private const val REQUEST_CODE = 1002

    fun scheduleNext(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Read interval from our canonical prefs (seconds)
        val prefs = PreferencesHelper(context)
        val intervalSec = max(10, prefs.hydrationReminderIntervalSec) // guard against 0/negatives
        val triggerAt = System.currentTimeMillis() + intervalSec * 1000L

        val intent = Intent(context, HydrationReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Exact vs inexact depending on permission/SDK
        val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            am.canScheduleExactAlarms()
        } else true

        try {
            if (canExact) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                } else {
                    am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                }
                Log.d(TAG, "Scheduled EXACT alarm in ${intervalSec}s")
            } else {
                // Fallback so reminders still work without the special permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                }
                Log.w(TAG, "Exact alarm permission not granted; scheduled INEXACT alarm in ${intervalSec}s")
            }
        } catch (se: SecurityException) {
            Log.e(TAG, "Failed to schedule alarm (security exception). Falling back to inexact.", se)
            // Last-resort fallback
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        }
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pi)
        Log.d(TAG, "Hydration reminders cancelled")
    }
}