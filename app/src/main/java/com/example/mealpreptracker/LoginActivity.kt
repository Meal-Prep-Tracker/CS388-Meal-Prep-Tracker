package com.example.mealpreptracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var email: EditText;
    private lateinit var password: EditText;
    private lateinit var normalLoginBtn: Button;
    private lateinit var googleLoginBtn: ImageButton

    private lateinit var googleSignInClient: GoogleSignInClient

    private val LOGIN_ACTIVITY_TAG = "LOGIN_ACTIVITY"

    private val EMAIL_REGEX = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        email = findViewById(R.id.login_email_input)
        password = findViewById(R.id.login_password_input)
        normalLoginBtn = findViewById(R.id.login_login_button)
        googleLoginBtn = findViewById(R.id.login_continue_w_google_btn)

        normalLoginBtn.setOnClickListener {
            val emailStr = email.text.toString()
            val passwordStr = password.text.toString()
            val emptyFields = mapOf(
                email to emailStr.isNotEmpty(),
                password to passwordStr.isNotEmpty(),
            ).filter { (_, isValid) -> !isValid }

            if(emptyFields.isNotEmpty()) {
                Toast.makeText(this@LoginActivity, "One or more empty fields", Toast.LENGTH_SHORT).show()
                emptyFields.forEach { (editText, _) -> editText.error = "This field is required!" }
                return@setOnClickListener
            }

            if(!EMAIL_REGEX.matches(emailStr)) {
                Toast.makeText(this@LoginActivity, "Invalid email", Toast.LENGTH_SHORT).show()
                email.error = "Please enter a valid email address."
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener { task ->
                if(!task.isSuccessful) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Unknown email and password.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(LOGIN_ACTIVITY_TAG, "AUTHENTICATION ERROR: " + task.exception.toString());
                }
                else {
                    val intent = Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    )
                    Toast.makeText(
                        this@LoginActivity,
                        "Successfully logged in!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(intent)
                }
            }
        }

        googleLoginBtn.setOnClickListener {
            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_token))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

            val intent = googleSignInClient.signInIntent
            googleSignInActivityResultLauncher.launch(intent)
        }
    }

    private val googleSignInActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if(result.resultCode == RESULT_OK) {
                Log.v(LOGIN_ACTIVITY_TAG, result.toString())
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogleAccount(account)
                }
                catch(e: ApiException) {
                    Log.e(LOGIN_ACTIVITY_TAG, e.toString())
                    Toast.makeText(this@LoginActivity, "Failed to log in with Google!", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Log.e(LOGIN_ACTIVITY_TAG, "resultCode != RESULT_OK: ${result.data}")
            }
        }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        val cred = GoogleAuthProvider.getCredential(account!!.idToken, null)

        auth.signInWithCredential(cred)
            .addOnSuccessListener {result ->
                Toast.makeText(this@LoginActivity, "Successfully logged in with Google!", Toast.LENGTH_SHORT).show()
                Log.v(LOGIN_ACTIVITY_TAG, result.toString())
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { err ->
                Log.e(LOGIN_ACTIVITY_TAG, err.toString())
                Toast.makeText(this@LoginActivity, "Failed to log in with Google!", Toast.LENGTH_SHORT).show()
            }
    }
}