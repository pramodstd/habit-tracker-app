package com.wellness.habittracker.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wellness.habittracker.R
import com.wellness.habittracker.fragments.HabitsFragment
import com.wellness.habittracker.fragments.MoodFragment
import com.wellness.habittracker.fragments.SettingsFragment
import com.wellness.habittracker.fragments.TrendsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Set a dark green status bar with white icons (readable)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_green_dark)

        // ✅ Ensure system draws system bar backgrounds
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // ✅ Force light icons (for dark background)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false // false = white icons

        // 🧭 Initialize Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Load default fragment
        loadFragment(HabitsFragment())

        // Handle navigation item clicks
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> {
                    loadFragment(HabitsFragment())
                    true
                }
                R.id.nav_mood -> {
                    loadFragment(MoodFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                R.id.nav_trends -> {
                    loadFragment(TrendsFragment())
                    true
                }
                else -> false
            }
        }
    }

    // 🔁 Helper to switch fragments
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}