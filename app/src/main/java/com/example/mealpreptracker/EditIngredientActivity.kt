package com.example.mealpreptracker

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestHeaders
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Headers
import org.json.JSONException


private const val TAG = "EditIngredientActivity"
//private const val SEARCH_API_KEY: String = BuildConfig.API_KEY
private const val INGREDIENT_SEARCH_URL =
    "https://api.calorieninjas.com/v1/nutrition"
class EditIngredientActivity : AppCompatActivity(){
    lateinit var ingredients: MutableList<Ingredient>
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ingredient)
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
        // Get the submit button
        val add_button = findViewById<Button>(R.id.addBtn)
        val done_button = findViewById<Button>(R.id.doneBtn)

        // Get the extra from the Intent
        val selected_meal = intent.getSerializableExtra(MEAL_EXTRA) as Meal

//        Log.w(TAG, selected_meal.toString())
        // Set up the API request code
        val client = AsyncHttpClient()

        // update the ingredients list by pulling in all hte meals from the firebase db - can add a listener that listens to changes in the collection
        val ingredientsReference = database.child("Ingredients").orderByChild("meal_id").equalTo(selected_meal.id)
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
//                        // TODO: Create the parsedJSON
//                        val parsedJson = Json.decodeFromString<BaseResponse>(json.jsonObject.toString())
//
//                        Log.w(TAG, parsedJson.items.toString())
                        // TODO: add a new ingredient to the Ingredients collection
                        val key = database.child("Ingredients").push().key
                        // error log
                        if (key == null) {
                            Log.w(TAG, "Couldn't get push key for ingredients")
                            return
                        }
                        // TODO: make a nutrition summary

                        // add an ingredient
                        addIngredient(key, selected_meal, name, quantity, price)

                    } catch (e: JSONException) {
                        Log.e(TAG, "Exception: $e")
                    }
                }

            })


        }

        // Set the add button onClickListener
        done_button.setOnClickListener{
            TODO("Need to implement the done listener, which will update the edited ingredients")
        }

    }

    private fun fetchIngredients(ingredientsReference: Query, ingredientAdapter: IngredientAdapter) {
        // Time to call the DB!!!!!!!!!!
        val ingredientListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ingredients.clear()
                // Get Meal object and use the values to update the UI
                for (childSnapshot in dataSnapshot.children) {
                    ingredients.add(
                        Ingredient(
                            id = childSnapshot.child("id").value.toString(),
                            meal_id = childSnapshot.child("meal_id").value.toString(),
                            quantity = childSnapshot.child("quantity").value.toString().toDoubleOrNull() ?: 0.0,
                            price = childSnapshot.child("price").value.toString().toDoubleOrNull() ?: 0.0,
                            name = childSnapshot.child("name").value.toString()
                        )
                    )
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
        price: String
    ) {
        // make nutritionSummary for the ingredient and add it to the ingredient object

        // Make a new ingredient and add it to the realtime DB
        val ingredient = Ingredient(
            id = key,
            meal_id = selected_meal.id,
            name = name,
            quantity = quantity.toDouble(),
            price = price.toDouble()
        )

        Log.w(TAG, ingredient.toString())
        // Add to the meals collection
        database.child("Ingredients").child(key).setValue(ingredient)
    }
}