package com.wellness.habittracker.models

data class Habit(
    val id: String,
    var name: String,
    var category: String,
    var icon: String,
    var goal: Int,
    var currentProgress: Int = 0,
    var unit: String,
    var streak: Int = 0,
    var lastCompleted: String? = null,
    var isCompleted: Boolean = false
) {
    fun getProgressPercentage(): Int {
        return if (goal > 0) {
            ((currentProgress.toFloat() / goal.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else 0
    }

    fun incrementProgress() {
        if (currentProgress < goal) {
            currentProgress++
        }
        if (currentProgress >= goal) {
            isCompleted = true
        }
    }

    fun decrementProgress() {
        if (currentProgress > 0) {
            currentProgress--
        }
        if (currentProgress < goal) {
            isCompleted = false
        }
    }
}
