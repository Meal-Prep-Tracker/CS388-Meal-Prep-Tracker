package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage

private const val TAG = "MealDetailActivity"

class MealDetailActivity : AppCompatActivity() {
    private lateinit var mealNameHeaderTextView: TextView
    private lateinit var mealPriceTextView: TextView
    private lateinit var mealServingsTextView: TextView
    private lateinit var calHeaderTextView: TextView
    private lateinit var fatHeaderTextView: TextView
    private lateinit var satFatHeaderTextView: TextView
    private lateinit var proteinHeaderTextView: TextView
    private lateinit var sodiumHeaderTextView: TextView
    private lateinit var potassiumHeaderTextView: TextView
    private lateinit var cholesterolHeaderTextView: TextView
    private lateinit var carbsHeaderTextView: TextView
    private lateinit var fiberHeaderTextView: TextView
    private lateinit var sugarHeaderTextView: TextView
    private  lateinit var summaryHeaderTextView: TextView
    private lateinit var foodImageView: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var sharedpreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val darkMode = sharedpreferences.getBoolean("darkMode", false)
        if (darkMode) {
            // Apply dark theme
            setTheme(R.style.mealPrepThemeDark)
        } else {
            // Apply light theme
            setTheme(R.style.mealPrepTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_detail)

        auth = FirebaseAuth.getInstance()
        auth.currentUser ?: run {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.putExtra(SOURCE_EXTRA, "MealDetailActivity")
            startActivity(intent)
            finish()
        }

        // Get the database reference
        database = Firebase.database.reference


        // TODO: Find the views for the screen
        mealNameHeaderTextView = findViewById(R.id.mealName)
        mealPriceTextView = findViewById(R.id.mealPrice)
        mealServingsTextView = findViewById(R.id.mealServings)
        summaryHeaderTextView = findViewById(R.id.summaryHeader)
        calHeaderTextView = findViewById(R.id.calHeader)
        fatHeaderTextView = findViewById(R.id.fatHeader)
        satFatHeaderTextView = findViewById(R.id.satFatHeader)
        proteinHeaderTextView = findViewById(R.id.proteinHeader)
        potassiumHeaderTextView = findViewById(R.id.potassiumHeader)
        cholesterolHeaderTextView = findViewById(R.id.cholesterolHeader)
        carbsHeaderTextView = findViewById(R.id.carbsHeader)
        fiberHeaderTextView = findViewById(R.id.fiberHeader)
        sugarHeaderTextView = findViewById(R.id.sugarHeader)
        sodiumHeaderTextView = findViewById(R.id.sodiumHeader)
        foodImageView = findViewById(R.id.foodImageView)
        // TODO: Get the extra from the Intent
        val meal = intent.getSerializableExtra(MEAL_EXTRA) as Meal
        // TODO: Set the mealNameHeader, meal details, and nutrition summary of the meal
        mealNameHeaderTextView.text = meal.name
        mealServingsTextView.text = "${meal.servings.toString()} servings"

        // TODO: setup the Recycler View
        val miniIngredientsRv = findViewById<RecyclerView>(R.id.miniMealIngredientsRv)

        database.child("Ingredients").orderByChild("meal_id").equalTo(meal.id).get()
            .addOnSuccessListener {
                    snapshot ->
                    // Get the ingredients of the meal u pass from Main Activity in your Intent
                    val ingredients = snapshot.children.map{
                            dataSnapshot ->  dataSnapshot.getValue(Ingredient::class.java)
                    }

                    // Create adapter passing in the list of emails
                    val adapter = DisplayIngredientAdapter(ingredients)
                    // Attach the adapter to the RecyclerView to populate items
                    miniIngredientsRv.adapter = adapter
                    // Set layout manager to position the items
                    miniIngredientsRv.layoutManager = LinearLayoutManager(this@MealDetailActivity)
                    Log.w(TAG, "Details of ${meal} will be shown now")

                    // Calculate all the details of the meal
                    calHeaderTextView.text = "Calories: ${ingredients.sumOf { it?.nutritionSummary?.calories ?: 0.0 }}"
                    proteinHeaderTextView.text = "Protein: ${ingredients.sumOf { it?.nutritionSummary?.protein ?: 0.0 }}g"
                    carbsHeaderTextView.text = "Carbs: ${ingredients.sumOf { it?.nutritionSummary?.carbohydrates ?: 0.0 }}g"
                    fatHeaderTextView.text = "Fat: ${ingredients.sumOf { it?.nutritionSummary?.fat ?: 0.0 }}g"
                    fiberHeaderTextView.text = "Fiber: ${ingredients.sumOf { it?.nutritionSummary?.fiber ?: 0.0 }}g"
                    sugarHeaderTextView.text = "Sugar: ${ingredients.sumOf { it?.nutritionSummary?.sugar ?: 0.0 }}g"
                    satFatHeaderTextView.text = "Saturated Fat: ${ingredients.sumOf { it?.nutritionSummary?.saturatedFat ?: 0.0 }}g"
                    sodiumHeaderTextView.text = "Sodium: ${ingredients.sumOf { it?.nutritionSummary?.sodium ?: 0.0 }}mg"
                    potassiumHeaderTextView.text = "Potassium: ${ingredients.sumOf { it?.nutritionSummary?.potassium ?: 0.0 }}mg"
                    cholesterolHeaderTextView.text = "Cholesterol: ${ingredients.sumOf { it?.nutritionSummary?.cholesterol ?: 0.0 }}mg"
                    mealPriceTextView.text = "\$${ingredients.sumOf { it?.price ?: 0.0 }}"

                    // Set the image of the meal if it even exists
                    if (meal.image_id != null)
                    {
                        // Get the storage reference
                        storage = com.google.firebase.ktx.Firebase.storage.reference
                        val imageRef = storage.child(meal.image_id!!)
                        imageRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                try {
                                    Glide
                                        .with(this)
                                        .load(uri)
                                        .apply(RequestOptions().override(300))
                                        .into(foodImageView)
                                }
                                catch(e: Exception) {
                                    Log.e(TAG, "Uri ${uri} is invalid!")
                                }
                            }
                            .addOnFailureListener {
                                Log.e(TAG, "Could not download image from Firebase!")
                            }
                    }
            }
            .addOnFailureListener{
                Log.e(TAG, "Error getting summary data", it)
            }
    }


}
