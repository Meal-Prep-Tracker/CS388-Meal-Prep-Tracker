package com.example.mealpreptracker

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditMealActivity : AppCompatActivity() {
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var mealName: EditText
    private lateinit var servings: EditText
    private lateinit var editIngredientsBtn: Button
    private lateinit var image: ImageButton

    private val EDIT_MEAL_TAG = "EDIT_MEAL"
    private val CAMERA_RESULT_CODE = 123;
    private val ACTIVITY_NAME = "EditMealActivity"

    private var imageRetaken = false;

    override fun onCreate(savedInstanceState: Bundle?) {
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

        image.setOnClickListener {
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
            if(imageRetaken) {
                val bitmapToSave = getImageBitmap(meal.id, uid)
                meal.image_id = "" // TODO: set this to the id of the saved image somewhere
            }

            saveMeal(meal)
        }
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        db = Firebase.database

        mealName = findViewById(R.id.meal_name_input)
        servings = findViewById(R.id.servings_input)
        editIngredientsBtn = findViewById(R.id.edit_ingredients_btn)
        image = findViewById(R.id.food_image);
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
            image.setImageBitmap(takenPic)
            imageRetaken = true
        }
    }

    private fun getImageBitmap(mealId: String, userId: String): Bitmap {
        val bitmap = Bitmap.createBitmap(image.drawingCache)
        image.isDrawingCacheEnabled = false

        // TODO insert bitmap into firebase, obtain the id of it and return it
        return bitmap
    }
}