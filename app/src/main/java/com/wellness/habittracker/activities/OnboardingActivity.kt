package com.wellness.habittracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.wellness.habittracker.R
import com.wellness.habittracker.adapters.OnboardingAdapter
import com.wellness.habittracker.models.OnboardingItem
import com.wellness.habittracker.utils.PreferencesHelper

class OnboardingActivity : AppCompatActivity() {

    private lateinit var btnSkip: TextView
    private lateinit var btnNext: Button
    private lateinit var viewPager: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var prefsHelper: PreferencesHelper
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        
        // FIX: Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_green_dark)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = 0
        }

        // IDs from your XML
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)
        viewPager = findViewById(R.id.viewPager)
        dotsLayout = findViewById(R.id.dotsLayout)

        prefsHelper = PreferencesHelper(this)

        // Create onboarding items
        val onboardingItems = listOf(
            OnboardingItem(
                title = "Track Your Habits",
                description = "Build better habits by tracking your daily progress and staying consistent",
                emoji = "📊"
            ),
            OnboardingItem(
                title = "Monitor Your Mood",
                description = "Keep track of how you're feeling each day and discover patterns",
                emoji = "😊"
            ),
            OnboardingItem(
                title = "Stay Hydrated",
                description = "Get reminders to drink water throughout the day and stay healthy",
                emoji = "💧"
            )
        )

        // Set up ViewPager2 adapter
        adapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = adapter

        // Set up dots indicator
        setupDots(onboardingItems.size)
        updateDots(0)

        // ViewPager2 page change callback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
                
                // Update button text on last page
                if (position == onboardingItems.size - 1) {
                    btnNext.text = getString(R.string.get_started)
                } else {
                    btnNext.text = getString(R.string.next)
                }
            }
        })

        btnSkip.setOnClickListener {
            prefsHelper.onboardingComplete = true
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnNext.setOnClickListener {
            val nextIndex = viewPager.currentItem + 1
            if (nextIndex < adapter.itemCount) {
                viewPager.currentItem = nextIndex
            } else {
                // Last page → continue to login
                prefsHelper.onboardingComplete = true
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun setupDots(count: Int) {
        dotsLayout.removeAllViews()
        val dots = Array(count) { ImageView(this) }
        
        for (i in dots.indices) {
            dots[i] = ImageView(this)
            dots[i].setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.indicator_dot_inactive)
            )
            
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dotsLayout.addView(dots[i], params)
        }
    }

    private fun updateDots(currentPosition: Int) {
        val childCount = dotsLayout.childCount
        for (i in 0 until childCount) {
            val imageView = dotsLayout.getChildAt(i) as ImageView
            if (i == currentPosition) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_dot_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_dot_inactive)
                )
            }
        }
    }
}