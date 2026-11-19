package com.wellness.habittracker.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wellness.habittracker.R
import com.wellness.habittracker.utils.PreferencesHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var btnContinue: Button
    private lateinit var prefsHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // FIX: Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_green_dark)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = 0
        }

        // IDs from your XML
        etName = findViewById(R.id.etName)
        btnContinue = findViewById(R.id.btnContinue)
        prefsHelper = PreferencesHelper(this)

        btnContinue.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_name_prompt), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefsHelper.userName = name
            // Onboarding should already be complete, but set just in case:
            prefsHelper.onboardingComplete = true

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}