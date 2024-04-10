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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditMealActivity : AppCompatActivity() {
    private var db: FirebaseDatabase = Firebase.database
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var mealName: EditText = findViewById(R.id.meal_name_input)
    private var servings: EditText = findViewById(R.id.servings_input)
    private var editIngredientsBtn: Button = findViewById(R.id.edit_ingredients_btn)
    private var image: ImageButton = findViewById(R.id.food_image);

    private val MEAL_ID = "meal_id"
    private val MEALS_COLLECTION = "Meals"
    private val EDIT_MEAL_TAG = "EDIT_MEAL"
    private val PIC_ID = 123;

    private var imageRetaken = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_meal)

        auth.currentUser
            ?.let { userLoggedInLogic }
            ?: run {
                startActivity(Intent(this@EditMealActivity, WelcomeActivity::class.java))
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PIC_ID) {
            val takenPic: Bitmap? = data!!.extras!!["data"] as Bitmap?
            image.setImageBitmap(takenPic)
            imageRetaken = true
        }
    }

    private val userLoggedInLogic: (FirebaseUser) -> Unit = { user ->
        val uid = user.uid
        val meal = intent.getSerializableExtra(MEAL_ID) as Meal
        meal.user_id = uid

        mealName.setText(meal.name)
        servings.setText(meal.servings.toString())

        image.setOnClickListener {
            val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camIntent, PIC_ID)
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
            meal.name = mealNameStr
            meal.servings = servingsInt

            val mealCollection = db.getReference(MEALS_COLLECTION)
            mealCollection.child(meal.id).setValue(meal)
                .addOnSuccessListener {
                    Log.v(EDIT_MEAL_TAG, "Successfuly saved meal ${meal.id} for user ${uid}.")
                    Toast.makeText(this@EditMealActivity, "Successfully saved meal information!", Toast.LENGTH_SHORT).show()
                    if(imageRetaken) {
                        val imageId: String = saveImage(meal.id, uid)
                        meal.image_id = imageId
                    }
                    startActivity(Intent(this@EditMealActivity, EditIngredientActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e(EDIT_MEAL_TAG, "Failed to save meal ${meal.id} for user ${uid}; exception: ${e}")
                    Toast.makeText(this@EditMealActivity, "Failed to save meal information.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveImage(mealId: String, userId: String): String {
        val bitmap = Bitmap.createBitmap(image.drawingCache)
        image.isDrawingCacheEnabled = false

        // TODO insert bitmap into firebase, obtain the id of it and return it
        return ""
    }
}