package com.wellness.habittracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.wellness.habittracker.R
import com.wellness.habittracker.activities.MainActivity
import com.wellness.habittracker.reminders.HydrationActionReceiver
import com.wellness.habittracker.utils.PreferencesHelper

class TodayWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            val views = buildViews(context)
            appWidgetManager.updateAppWidget(id, views)
        }
    }
    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, TodayWidgetProvider::class.java))
            if (ids != null && ids.isNotEmpty()) {
                for (id in ids) {
                    val views = buildViews(context)
                    manager.updateAppWidget(id, views)
                }
            }
        }
        private fun buildViews(context: Context): RemoteViews {
            val prefs = PreferencesHelper(context)
            val views = RemoteViews(context.packageName, R.layout.today_widget)
            views.setTextViewText(R.id.tvWidgetHydration, context.getString(R.string.widget_water_fmt, prefs.hydrationCount, prefs.hydrationGoal))
            val intentOpen = Intent(context, MainActivity::class.java)
            val piOpen = PendingIntent.getActivity(context, 50101, intentOpen, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.rootWidget, piOpen)
            val intentInc = Intent(context, HydrationActionReceiver::class.java).apply { action = HydrationActionReceiver.ACTION_INCREMENT }
            val piInc = HydrationActionReceiver.pendingIntent(context, intentInc)
            views.setOnClickPendingIntent(R.id.btnWidgetAddGlass, piInc)
            return views
        }
    }
}
