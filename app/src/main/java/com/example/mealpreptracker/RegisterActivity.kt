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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var fname: EditText;
    private lateinit var lname: EditText;
    private lateinit var email: EditText;
    private lateinit var password: EditText;
    private lateinit var confirm: EditText;
    private lateinit var normSignUpBtn: Button
    private lateinit var googleSignUpBtn: ImageButton

    private val EMAIL_REGEX = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
    private val USERS_COLLECTION = "users"

    private val REGISTER_ACTIVITY_TAG = "REGISTER_ACTIVITY"
    private val REGISTER_SUCCESS = "Successfully signed up!"
    private val REGISTER_FAILED = "Failed to sign up."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseDatabase = Firebase.database
        auth = FirebaseAuth.getInstance()

        fname = findViewById(R.id.first_name_input)
        lname = findViewById(R.id.last_name_input)
        email = findViewById(R.id.email_input)
        password = findViewById(R.id.password_input)
        confirm = findViewById(R.id.conf_password_input)

        normSignUpBtn = findViewById(R.id.sign_up_button)
        googleSignUpBtn = findViewById(R.id.sign_up_btn_google)

        normSignUpBtn.setOnClickListener {
            val emptyFields = mapOf(
                fname to fname.text.toString().isNotEmpty(),
                lname to lname.text.toString().isNotEmpty(),
                email to email.text.toString().isNotEmpty(),
                password to password.text.toString().isNotEmpty(),
                confirm to confirm.text.toString().isNotEmpty(),
            ).filter { (_, isValid) -> !isValid }

            if(emptyFields.isNotEmpty()) {
                Toast.makeText(this@RegisterActivity, "One or more empty fields", Toast.LENGTH_SHORT).show()
                emptyFields.forEach { (editText, _) -> editText.error = "This field is required!" }
                return@setOnClickListener
            }

            if(!EMAIL_REGEX.matches(email.text.toString())) {
                Toast.makeText(this@RegisterActivity, "Invalid email", Toast.LENGTH_SHORT).show()
                email.error = "Please enter a valid email address."
                return@setOnClickListener
            }

            val confMatchesPass = password.text.toString() == confirm.text.toString()
            if(!confMatchesPass) {
                Toast.makeText(this@RegisterActivity, "Passwords don't match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(REGISTER_ACTIVITY_TAG, "Beginning authentication.")
            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener { task ->
                    Log.d(REGISTER_ACTIVITY_TAG, "AUTHENTICATION RESULT: " + task.result.toString())

                    if(!task.isSuccessful) {
                        toastMsg(REGISTER_FAILED)
                        Log.e(REGISTER_ACTIVITY_TAG, "AUTHENTICATION ERROR: " + task.exception.toString());
                    }
                    else {
                        val uid = task.result.user!!.uid
                        insertUserToDb(User(
                            uid = uid,
                            firstName = fname.text.toString(),
                            lastName = lname.text.toString(),
                            email = email.text.toString()
                        ))
                        toastMsg(REGISTER_SUCCESS)
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(REGISTER_ACTIVITY_TAG, e.toString())
                    toastMsg(REGISTER_FAILED)
                }
        }

        googleSignUpBtn.setOnClickListener {
            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_token))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
            googleSignInActivityResultLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private val googleSignInActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(REGISTER_ACTIVITY_TAG, "onActivityResult : ${result.data!!.extras}")

                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = accountTask.getResult(ApiException::class.java)
                    Log.d(REGISTER_ACTIVITY_TAG, "onActivityResult : $account")

                    toastMsg(REGISTER_SUCCESS)

                    firebaseAuthWithGoogleAccount(account)
                } catch (e: ApiException) {
                    toastMsg(REGISTER_FAILED)
                    Log.w(REGISTER_ACTIVITY_TAG, "onActivityResult : ${e.message}")
                }
            } else {
                toastMsg(REGISTER_FAILED)
                Log.w(REGISTER_ACTIVITY_TAG, "onActivityResult : ${result}")
            }
        }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { authRes ->
                Log.d(REGISTER_ACTIVITY_TAG, "firebaseAuthWithGoogleAccount : ${authRes.user}")

                val authUser = authRes.user!!
                val uid = authUser.uid
                val email = authUser.email!!
                insertUserToDb(User(
                    uid = uid,
                    firstName = account.givenName ?: "",
                    lastName = account.familyName ?: "",
                    email = email
                ))

                toastMsg(REGISTER_SUCCESS)

                val intent = Intent(
                    this@RegisterActivity,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { err ->
                Log.d(REGISTER_ACTIVITY_TAG, "firebaseAuthWithGoogleAccount : ${err.message}")
                toastMsg(REGISTER_FAILED)
            }
    }

    private fun insertUserToDb(user: User) {
        val usersCollection = firebaseDatabase.getReference(USERS_COLLECTION)
        usersCollection.child(user.uid).setValue(user)
    }

    private fun toastMsg(msg: String) {
        Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
    }
}