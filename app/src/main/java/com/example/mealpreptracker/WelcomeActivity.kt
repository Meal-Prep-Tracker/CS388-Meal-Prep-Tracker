package com.example.mealpreptracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        intent.getSerializableExtra("source")?.let {
            Toast.makeText(this, "You must be logged in to visit that page.", Toast.LENGTH_SHORT).show()
        }

        val loginBtn: Button = findViewById(R.id.welcome_login_button)
        val registerBtn: Button = findViewById(R.id.welcome_signup_button)

        loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}