package com.example.mealpreptracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

lateinit var auth: FirebaseAuth
private const val TAG = "UpdateProfile"


class UpdateProfileActivity : AppCompatActivity()  {
    private lateinit var formFirstNameText: EditText
    private lateinit var formLastNameText: EditText
    private lateinit var resetPasswordButton: Button
    private lateinit var updateButton: Button
    lateinit var sharedpreferences: SharedPreferences
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val darkMode = sharedpreferences.getBoolean("darkMode", false)
        val notifications = sharedpreferences.getBoolean("notifications", true)

        if (darkMode) {
            // Apply dark theme
            setTheme(R.style.mealPrepThemeDark)
        } else {
            // Apply light theme
            setTheme(R.style.mealPrepTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        formFirstNameText = findViewById(R.id.formFirstNameText)
        formLastNameText = findViewById(R.id.formLastNameText)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)
        updateButton = findViewById(R.id.updateButton)
        auth = FirebaseAuth.getInstance()

        database = Firebase.database.reference

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid ?: run {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.putExtra(SOURCE_EXTRA, "UpdateProfileActivity")
            startActivity(intent)
            finish()
        }

        var email = ""
        database.child("users").orderByChild("uid").equalTo(uid).get()
            .addOnSuccessListener {
                    snapshot ->

                val userData = snapshot.children.map{
                        dataSnapshot ->  dataSnapshot.getValue(User::class.java)
                }

                var fname = userData.map { it?.firstName }.joinToString(", ")
                var lname = userData.map { it?.lastName }.joinToString(", ")

                Log.v(TAG, fname)
                Log.v(TAG, lname)
                email = userData.map { it?.email }.joinToString(", ")

                formFirstNameText.setText(fname)
                formLastNameText.setText(lname)
            }
            .addOnFailureListener{
                Log.e(TAG, "Error getting summary data", it)
            }

        resetPasswordButton.setOnClickListener {
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@UpdateProfileActivity,
                        "We have sent you instructions to reset your password!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@UpdateProfileActivity,
                        "Failed to send reset email!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        updateButton.setOnClickListener {
            val firstName = formFirstNameText.getText().toString()
            val lastName = formLastNameText.getText().toString()

            uid?.let { userId ->
                // Update the user data in Firebase Realtime Database
                val userRef = database.child("users").child(userId)
                userRef.child("firstName").setValue(firstName)
                userRef.child("lastName").setValue(lastName)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this@UpdateProfileActivity,
                            "Profile updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error updating profile", exception)
                        Toast.makeText(
                            this@UpdateProfileActivity,
                            "Failed to update profile!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}