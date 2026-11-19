package com.wellness.habittracker.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wellness.habittracker.R
import com.wellness.habittracker.utils.PreferencesHelper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // FIX: Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_green_dark)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = 0
        }
        
        val prefs = PreferencesHelper(this)
        val next = if (prefs.onboardingComplete && prefs.userName.isNotEmpty()) {
            Intent(this, MainActivity::class.java)
        } else if (!prefs.onboardingComplete) {
            Intent(this, OnboardingActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(next)
        finish()
    }
}
