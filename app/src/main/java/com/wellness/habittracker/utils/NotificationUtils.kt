package com.wellness.habittracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wellness.habittracker.R
import com.wellness.habittracker.reminders.HydrationActionReceiver

object NotificationUtils {
    private const val CH_ID = "hydration_channel"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(CH_ID, "Hydration", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(ch)
        }
    }

    fun showHydrationNotification(context: Context) {
        ensureChannel(context)
        val actionIntent = Intent(context, HydrationActionReceiver::class.java).apply {
            action = HydrationActionReceiver.ACTION_INCREMENT
        }
        val pi = PendingIntent.getBroadcast(context, 30205, actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(context, CH_ID)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle(context.getString(R.string.hydration_time))
            .setContentText(context.getString(R.string.hydration_tap_to_add))
            .addAction(0, context.getString(R.string.add_glass), pi)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1001, notif)
    }
}