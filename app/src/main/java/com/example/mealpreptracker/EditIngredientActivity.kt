package com.example.mealpreptracker

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestHeaders
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.Gson
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONException


private const val TAG = "EditIngredientActivity"
//private const val SEARCH_API_KEY: String = BuildConfig.API_KEY
private const val INGREDIENT_SEARCH_URL =
    "https://api.calorieninjas.com/v1/nutrition"
class EditIngredientActivity : AppCompatActivity(){
    lateinit var ingredients: MutableList<Ingredient>
    private lateinit var database: DatabaseReference
    lateinit var sharedpreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

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
        setContentView(R.layout.activity_edit_ingredient)

        auth = FirebaseAuth.getInstance()
        auth.currentUser ?: run {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.putExtra(SOURCE_EXTRA, "ProfileFragment")
            startActivity(intent)
            finish()
        }

        // Lookup the RecyclerView in activity layout
        val mealIngredientsRv = findViewById<RecyclerView>(R.id.mealIngredientsRv)
        // Make empty list of mealIngredients
        ingredients = mutableListOf()
        // Get the database reference
        database = Firebase.database.reference
        // Create adapter passing in the list of emails
        val ingredientAdapter = IngredientAdapter(this, ingredients, database)
        // Attach the adapter to the RecyclerView to populate items
        mealIngredientsRv.adapter = ingredientAdapter
        // Set layout manager to position the items
        mealIngredientsRv.layoutManager = LinearLayoutManager(this)
        // Get the buttons
        val add_button = findViewById<Button>(R.id.addBtn)
        val done_button = findViewById<Button>(R.id.doneBtn)

        // Get the extra from the Intent
        val selected_meal = intent.getSerializableExtra(MEAL_EXTRA) as Meal?

        // Set the header to the name of the meal
        val mealNameHeaderTextView = findViewById<TextView>(R.id.mealNameHeader)
        mealNameHeaderTextView.text = "${selected_meal?.name}"

        Log.w(TAG, selected_meal.toString())
        // Set up the API request code
        val client = AsyncHttpClient()

        // update the ingredients list by pulling in all hte meals from the firebase db - can add a listener that listens to changes in the collection
        val ingredientsReference = database.child(INGREDIENTS_COLLECTION).orderByChild("meal_id").equalTo(selected_meal?.id)
        fetchIngredients(ingredientsReference, ingredientAdapter)

        // Set the add button onClickListener
        add_button.setOnClickListener{
            // Get the quantity of the ingredient you want to add and it's name
            val quantity = findViewById<EditText>(R.id.ingredientAmt).text.toString()
            val name = findViewById<EditText>(R.id.ingredientNameField).text.toString()
            val price = findViewById<EditText>(R.id.ingredientPriceField).text.toString()
            val params = RequestParams()
            params.put("query", "${quantity}g of ${name}")

            val headers = RequestHeaders()
            headers.put("X-Api-Key", "kMmESaB5q81xKoBKO8BEVA==bXTUuChvmIaFY557")
            // Make an HTTP request - to get the nutrition summary per ingredient
            client.get(INGREDIENT_SEARCH_URL, headers, params, object : JsonHttpResponseHandler() {
                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String?,
                    throwable: Throwable?
                ) {
                    Log.e(TAG, "Failed to fetch articles: $statusCode")
                }

                override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                    Log.i(TAG, "Successfully fetched ingredient summaries: $json")
                    try {
                        // TODO: Create the parsedJSON
                        val summaryList = json.jsonObject.get("items") as JSONArray

                        // TODO: make a nutrition summary
                        Log.w(TAG, "Summary: ${summaryList.get(0).toString()}")

                        val summary = Gson().fromJson(summaryList.get(0).toString(),NutritionSummary::class.java)

                        Log.w(TAG, "Fetched summary for $name: ${summary.toString()}")
                        // TODO: add a new ingredient to the Ingredients collection
                        val key = database.child("Ingredients").push().key
                        // error log
                        if (key == null) {
                            Log.w(TAG, "Couldn't get push key for ingredients")
                            return
                        }

                        // add an ingredient
                        if (selected_meal != null) {
                            addIngredient(key, selected_meal, name, quantity, price, summary)
                        }

                    } catch (e: JSONException) {
                        Log.e(TAG, "Exception: $e")
                    }
                }

            })
        }

        // Set the add button onClickListener
        done_button.setOnClickListener{
            // update all ingredients
            val updates = ingredients.associateBy { it.id }
            Log.w(TAG, updates.toString())
            database.child(INGREDIENTS_COLLECTION).updateChildren(updates)

//            super.onBackPressed()
            finish()

//            val intent = Intent(this, MealListFragment::class.java)
//            startActivity(intent)

//            val fragmentManager = supportFragmentManager
//            val fragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.main_layout, MealListFragment())
//            fragmentTransaction.commit()

            // Route to MealsListFragment
//            val fragmentManager: FragmentManager = supportFragmentManager // For support library
//            if (fragmentManager.backStackEntryCount > 0) {
//                fragmentManager.popBackStack();
//            }
        }

    }

    private fun fetchIngredients(ingredientsReference: Query, ingredientAdapter: IngredientAdapter) {
        // Time to call the DB!!!!!!!!!!
        val ingredientListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ingredients.clear()
                // Get Meal object and use the values to update the UI
                for (childSnapshot in dataSnapshot.children) {
                    childSnapshot.getValue(Ingredient::class.java)?.let {
                        ingredients.add(
                            it
                        )
                    }
                }
                ingredientAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        ingredientsReference.addValueEventListener(ingredientListener)
    }
    private fun addIngredient(
        key: String,
        selected_meal: Meal,
        name: String,
        quantity: String,
        price: String,
        summary: NutritionSummary
    ) {
        // make nutritionSummary for the ingredient and add it to the ingredient object

        // Make a new ingredient and add it to the realtime DB
        val ingredient = Ingredient(
            id = key,
            meal_id = selected_meal.id,
            name = name,
            quantity = quantity.toDouble(),
            price = price.toDouble(),
            nutritionSummary = summary
        )

        Log.w(TAG, ingredient.toString())
        // Add to the meals collection
        database.child(INGREDIENTS_COLLECTION).child(key).setValue(ingredient)
    }
}