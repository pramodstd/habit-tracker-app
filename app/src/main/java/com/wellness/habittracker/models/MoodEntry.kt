package com.wellness.habittracker.models


import java.util.UUID

data class MoodEntry(

          // "HH:mm"
    val mood: Int,
                           // 1..5

    val timestamp: Long = System.currentTimeMillis(),  // epoch millis
    val date: String = "",               // e.g. "2025-10-07"
    val time: String = "",               // e.g. "13:47"
    val id: String = UUID.randomUUID().toString(),// 1..5


                     // 1..5
    val note: String? = null,

) {
    fun label(): String = when (mood) {
        5 -> "Excellent"
        4 -> "Good"
        3 -> "Okay"
        2 -> "Poor"
        1 -> "Bad"
        else -> "Okay"
    }
    fun emoji(): String = when (mood) {
        5 -> "😄"
        4 -> "🙂"
        3 -> "😐"
        2 -> "😕"
        1 -> "😢"
        else -> "😐"
    }
}