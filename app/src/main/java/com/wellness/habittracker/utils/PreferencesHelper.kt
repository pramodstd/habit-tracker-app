package com.wellness.habittracker.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wellness.habittracker.models.Habit
import com.wellness.habittracker.models.MoodEntry
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.max

class PreferencesHelper(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("wellness_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    // -------- Keys --------
    private object K {
        const val HABITS = "habits_json"
        const val MOOD_ENTRIES = "mood_entries_json"

        const val HYDRATION_GOAL = "hydration_goal"
        const val HYDRATION_COUNT = "hydration_count"
        const val HYDRATION_REMINDER = "hydration_reminder"
        const val HYDRATION_INTERVAL_SEC = "hydration_interval_sec"

        const val GOAL_DEADLINES = "goal_deadlines"
        const val WEEKLY_REPORTS = "weekly_reports"
        const val MOTIVATIONAL_QUOTES = "motivational_quotes"
        const val SLEEP_REMINDER = "sleep_reminder"

        const val USER_NAME = "user_name"
        const val ONBOARDING_DONE = "onboarding_complete"
    }

    // -------- Simple helpers --------
    private fun getStringOrNull(obj: JSONObject, key: String): String? {
        return if (obj.has(key) && !obj.isNull(key)) obj.optString(key) else null
    }

    private fun parseOrNull(fmt: SimpleDateFormat, s: String): Long? =
        try { fmt.parse(s)?.time } catch (_: ParseException) { null } catch (_: Exception) { null }

    // -------- Habits --------
    fun getHabits(): MutableList<Habit> {
        val json = prefs.getString(K.HABITS, "[]") ?: "[]"
        return try {
            val type = object : TypeToken<MutableList<Habit>>() {}.type
            gson.fromJson<MutableList<Habit>>(json, type) ?: mutableListOf()
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    fun saveHabits(habits: List<Habit>) {
        prefs.edit().putString(K.HABITS, gson.toJson(habits)).apply()
    }

    // -------- Mood (normalized to epoch millis) --------
    fun addMoodEntry(entry: MoodEntry) {
        val list = getMoodEntries()
        list.add(entry)
        prefs.edit().putString(K.MOOD_ENTRIES, gson.toJson(list)).apply()
    }

    fun saveMoodEntries(list: List<MoodEntry>) {
        prefs.edit().putString(K.MOOD_ENTRIES, gson.toJson(list)).apply()
    }

    fun getMoodEntries(): MutableList<MoodEntry> {
        val json = prefs.getString(K.MOOD_ENTRIES, "[]") ?: "[]"

        // Try normal parse first
        runCatching {
            val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
            return gson.fromJson<MutableList<MoodEntry>>(json, type) ?: mutableListOf()
        }

        // Fallback: migrate legacy/string timestamps
        return migrateMoodEntries(json)
    }

    private fun migrateMoodEntries(json: String): MutableList<MoodEntry> {
        val out = mutableListOf<MoodEntry>()
        val arr = runCatching { JSONArray(json) }.getOrElse { JSONArray() }

        val localFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val isoNoMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val isoWithMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun parseToMillis(v: Any?): Long {
            return when (v) {
                is Number -> v.toLong()
                is String -> {
                    val s = v.trim()
                    parseOrNull(localFmt, s)
                        ?: parseOrNull(isoWithMillis, s)
                        ?: parseOrNull(isoNoMillis, s)
                        ?: System.currentTimeMillis()
                }
                else -> System.currentTimeMillis()
            }
        }

        for (i in 0 until arr.length()) {
            val o: JSONObject = arr.optJSONObject(i) ?: continue

            val mood = when {
                o.has("mood") -> max(1, o.optInt("mood", 3))
                o.has("rating") -> max(1, o.optInt("rating", 3)) // legacy field
                else -> 3
            }

            val note = getStringOrNull(o, "note")?.takeIf { it.isNotBlank() }

            val ts = parseToMillis(
                when {
                    o.has("timestamp") -> o.opt("timestamp")
                    o.has("timeMillis") -> o.opt("timeMillis") // another legacy field
                    else -> null
                }
            )

            val dateStr = getStringOrNull(o, "date") ?: dateFmt.format(Date(ts))
            val timeStr = getStringOrNull(o, "time") ?: timeFmt.format(Date(ts))
            val id = getStringOrNull(o, "id") ?: UUID.randomUUID().toString()

            out.add(
                MoodEntry(
                    mood = mood,
                    note = note,
                    timestamp = ts,
                    date = dateStr,
                    time = timeStr,
                    id = id
                )
            )
        }

        // Persist normalized data so future reads are safe/fast
        prefs.edit().putString(K.MOOD_ENTRIES, gson.toJson(out)).apply()
        return out
    }

    // -------- Hydration prefs --------
    var hydrationGoal: Int
        get() = prefs.getInt(K.HYDRATION_GOAL, 8).coerceAtLeast(1)
        set(v) { prefs.edit().putInt(K.HYDRATION_GOAL, v.coerceAtLeast(1)).apply() }

    var hydrationCount: Int
        get() = prefs.getInt(K.HYDRATION_COUNT, 0).coerceAtLeast(0)
        set(v) { prefs.edit().putInt(K.HYDRATION_COUNT, v.coerceAtLeast(0)).apply() }

    var hydrationReminder: Boolean
        get() = prefs.getBoolean(K.HYDRATION_REMINDER, false)
        set(v) { prefs.edit().putBoolean(K.HYDRATION_REMINDER, v).apply() }

    var hydrationReminderIntervalSec: Int
        get() = prefs.getInt(K.HYDRATION_INTERVAL_SEC, 600)
        set(v) { prefs.edit().putInt(K.HYDRATION_INTERVAL_SEC, v).apply() }

    var goalDeadlines: Boolean
        get() = prefs.getBoolean(K.GOAL_DEADLINES, true)
        set(v) { prefs.edit().putBoolean(K.GOAL_DEADLINES, v).apply() }

    var weeklyReports: Boolean
        get() = prefs.getBoolean(K.WEEKLY_REPORTS, false)
        set(v) { prefs.edit().putBoolean(K.WEEKLY_REPORTS, v).apply() }

    var motivationalQuotes: Boolean
        get() = prefs.getBoolean(K.MOTIVATIONAL_QUOTES, true)
        set(v) { prefs.edit().putBoolean(K.MOTIVATIONAL_QUOTES, v).apply() }

    var sleepReminder: Boolean
        get() = prefs.getBoolean(K.SLEEP_REMINDER, false)
        set(v) { prefs.edit().putBoolean(K.SLEEP_REMINDER, v).apply() }

    // -------- Login / Onboarding prefs used by activities --------
    var userName: String
        get() = prefs.getString(K.USER_NAME, "") ?: ""
        set(v) { prefs.edit().putString(K.USER_NAME, v).apply() }

    var onboardingComplete: Boolean
        get() = prefs.getBoolean(K.ONBOARDING_DONE, false)
        set(v) { prefs.edit().putBoolean(K.ONBOARDING_DONE, v).apply() }
}