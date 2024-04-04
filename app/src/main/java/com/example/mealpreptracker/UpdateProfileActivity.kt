package com.example.mealpreptracker

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateProfileActivity : AppCompatActivity()  {
    private lateinit var formFirstNameText: EditText
    private lateinit var formLastNameText: EditText
    private lateinit var formEmailText: EditText
    private lateinit var formUsernameText: EditText
    private lateinit var formPasswordText: EditText
    private lateinit var formConfirmPasswordText: EditText
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        formFirstNameText = findViewById(R.id.formFirstNameText)
        formLastNameText = findViewById(R.id.formLastNameText)
        formEmailText = findViewById(R.id.formEmailText)
        formUsernameText = findViewById(R.id.formUsernameText)
        formPasswordText = findViewById(R.id.formPasswordText)
        formConfirmPasswordText = findViewById(R.id.formConfirmPasswordText)
        updateButton = findViewById(R.id.updateButton)


        updateButton.setOnClickListener {
            val firstName = formFirstNameText.getText().toString()
            val lastName = formLastNameText.getText().toString()
            val email = formEmailText.getText().toString()
            val username = formUsernameText.getText().toString()

            if (formPasswordText.text.toString().isNotEmpty()) {
                val password = formPasswordText.toString()
                val confirmPassword = formConfirmPasswordText.getText().toString()

                if (password != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }

            formFirstNameText.getText().clear()
            formLastNameText.getText().clear()
            formEmailText.getText().clear()
            formUsernameText.getText().clear()
            formPasswordText.getText().clear()
            formConfirmPasswordText.getText().clear()

            // DAO to update database

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

}