package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@SuppressLint("SimpleDateFormat")
private val dateFormat = SimpleDateFormat("MM/dd/yyyy")
class EditMealActivity : AppCompatActivity() {
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var mealName: EditText
    private lateinit var servings: EditText
    private lateinit var editIngredientsBtn: Button
    private lateinit var imageButton: ImageButton
    private lateinit var mealDate: TextView

    private val EDIT_MEAL_TAG = "EDIT_MEAL"
    private val CAMERA_RESULT_CODE = 123;
    private val ACTIVITY_NAME = "EditMealActivity"

    private var imageRetaken = false;

    lateinit var sharedpreferences: SharedPreferences

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
        setContentView(R.layout.activity_edit_meal)

        init()

        auth.currentUser ?: run {
            val intent = Intent(this@EditMealActivity, WelcomeActivity::class.java)
            intent.putExtra(SOURCE_EXTRA, ACTIVITY_NAME)
            startActivity(intent)
            finish()
        }

        val uid = auth.currentUser!!.uid
        val meal = intent.getSerializableExtra(MEAL_EXTRA) as Meal
        meal.user_id = uid

        mealName.setText(meal.name)
        servings.setText(meal.servings.toString())

        // Convert epoch time to Date
        val date = meal.date?.let { Date(it) }

        // Format the date
        val formattedDate = date?.let { dateFormat.format(it) }

        // Set the date in the TextView
        mealDate.text = formattedDate

        if(meal.image_id != null && meal.image_id!!.isNotEmpty()) {
            val storage = Firebase.storage.reference
            val imageId = meal.image_id!!
            val imageRef = storage.child(imageId)
            imageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    try {
                        Glide
                            .with(this)
                            .load(uri)
                            .apply(RequestOptions().override(300))
                            .into(imageButton)
                    }
                    catch(e: Exception) {
                        Log.e(EDIT_MEAL_TAG, "Uri ${uri} is invalid!")
                    }
                }
                .addOnFailureListener {
                    Log.e(EDIT_MEAL_TAG, "Could not download image from Firebase!")
                }
        }

        imageButton.setOnClickListener {
            val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camIntent, CAMERA_RESULT_CODE)
        }
        editIngredientsBtn.setOnClickListener {
            val mealNameStr = mealName.text.toString()
            val servingsStr = servings.text.toString()
            val emptyFields = mapOf(
                mealName to mealNameStr.isNotEmpty(),
                servings to servingsStr.isNotEmpty()
            ).filter { (_, isValid) -> !isValid }

            if(emptyFields.isNotEmpty()) {
                Toast.makeText(this@EditMealActivity, "One or more empty fields", Toast.LENGTH_SHORT).show()
                emptyFields.forEach { (editText, _) -> editText.error = "This field is required!" }
                return@setOnClickListener
            }

            val servingsInt = servingsStr.toInt()
            if(servingsInt < 0) {
                Toast.makeText(this@EditMealActivity, "Please enter a valid number of servings.", Toast.LENGTH_SHORT).show()
                servings.error = "Number of servings cannot be negative!"
                return@setOnClickListener
            }

            meal.name = mealNameStr
            meal.servings = servingsInt

            // Set the date of the meal from mealDate TextView
            meal.date = dateFormat.parse(mealDate.text.toString())?.time

            if(imageRetaken) {
                val storage = Firebase.storage.reference
                val bitmapToSave = getImageBitmap()
                val objLocation = "${IMAGES_COLLECTION}/${UUID.randomUUID()}.jpeg"
                val storedImage = storage.child(objLocation)
                val bytes = ByteArrayOutputStream()
                bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
                val path = Images.Media.insertImage(contentResolver, bitmapToSave, objLocation, "")
                storedImage.putFile(Uri.parse(path))
                    .addOnSuccessListener { task ->
                        task.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                            val downloadURL = downloadUri.toString()
                            Log.v(EDIT_MEAL_TAG, "Successfully saved image to Firebase Storage: ${downloadURL}")
                            meal.image_id = objLocation
                            saveMeal(meal)
                        }
                    }
                    .addOnFailureListener {e ->
                        Log.e(EDIT_MEAL_TAG, "Failed to save image to Firebase Storage: ${e}")
                        meal.image_id = ""
                        saveMeal(meal)
                    }
            }
            else {
                saveMeal(meal)
            }
        }
        mealDate.setOnClickListener {
            val cc = object : DatePickerFragment.OnDateSelectListener {
                @SuppressLint("SetTextI18n")
                override fun onDateSelect(c: Calendar) {
                    mealDate.text = "${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.DAY_OF_MONTH)}/${
                        c.get(Calendar.YEAR)
                    }"
                }
            }
            val newFragment = DatePickerFragment(cc)
            newFragment.show(supportFragmentManager, "datePicker")
        }
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        db = Firebase.database

        mealName = findViewById(R.id.meal_name_input)
        servings = findViewById(R.id.servings_input)
        editIngredientsBtn = findViewById(R.id.edit_ingredients_btn)
        imageButton = findViewById(R.id.food_image);
        mealDate = findViewById(R.id.mealDate)
    }

    private fun saveMeal(meal: Meal) {
        val mealCollection = db.getReference(MEALS_COLLECTION)
        mealCollection.child(meal.id).setValue(meal)
            .addOnSuccessListener {
                Log.v(EDIT_MEAL_TAG, "Successfuly saved meal ${meal.id} for user ${meal.user_id}.")
                val intent = Intent(this@EditMealActivity, EditIngredientActivity::class.java)
                intent.putExtra(MEAL_EXTRA, meal)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e(EDIT_MEAL_TAG, "Failed to save meal ${meal.id} for user ${meal.user_id}; exception: ${e}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAMERA_RESULT_CODE) {
            val takenPic: Bitmap? = data!!.extras!!["data"] as Bitmap?
//            imageButton.background = null
            Glide
                .with(this)
                .asBitmap()
                .load(takenPic)
                .apply(RequestOptions().override(300))
                .into(imageButton)
            imageRetaken = true
        }
    }

    private fun getImageBitmap(): Bitmap {
        return imageButton.drawable.toBitmap()
    }
}