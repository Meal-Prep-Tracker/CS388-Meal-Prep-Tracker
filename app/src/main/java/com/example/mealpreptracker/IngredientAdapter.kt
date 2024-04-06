package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestHeaders
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONException

private const val TAG = "IngredientAdapter"
private const val INGREDIENT_SEARCH_URL =
    "https://api.calorieninjas.com/v1/nutrition"
class IngredientAdapter(private val context: Context, private val ingredients: List<Ingredient>, private val databaseReference: DatabaseReference) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientAdapter.ViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient)
    }

    override fun getItemCount() = ingredients.size
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val ingredientNameEditText = itemView.findViewById<EditText>(R.id.ingredientName)
        private val ingredientQuantityEditText = itemView.findViewById<EditText>(R.id.amtInGrams)
        private val ingredientPriceEditText = itemView.findViewById<EditText>(R.id.ingredientPrice)
        private val deleteIconImageView = itemView.findViewById<ImageView>(R.id.deleteIngredientIcon)

        //        init {
//            itemView.setOnClickListener(this)
//        }
        @SuppressLint("SetTextI18n")
        fun bind(ingredient: Ingredient) {
            ingredientNameEditText.setText(ingredient.name)
            ingredientQuantityEditText.setText(ingredient.quantity.toString())
            ingredientPriceEditText.setText(ingredient.price.toString())
            val client = AsyncHttpClient()
            val headers = RequestHeaders()
            headers.put("X-Api-Key", "kMmESaB5q81xKoBKO8BEVA==bXTUuChvmIaFY557")

            // Set TextWatchers on all EditText
            ingredientNameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // Update the corresponding item in the list
                    ingredient.name = s.toString()
                    // Update the summary
                    val params = RequestParams()
                    params.put("query", "${ingredient.quantity}g of ${ingredient.name}")
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
                                val summary = Gson().fromJson(summaryList.get(0).toString(),NutritionSummary::class.java)
                                // Set the new summary to the ingredient
                                ingredient.nutritionSummary = summary

                            } catch (e: JSONException) {
                                Log.e(TAG, "Exception: $e")
                            }
                        }

                    })

                }
            })

            ingredientQuantityEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // Update the corresponding item in the list
                    ingredient.quantity = s.toString().toDouble()
                    // Update the summary
                    val params = RequestParams()
                    params.put("query", "${ingredient.quantity}g of ${ingredient.name}")
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
                                val summary = Gson().fromJson(summaryList.get(0).toString(),NutritionSummary::class.java)
                                // Set the new summary to the ingredient
                                ingredient.nutritionSummary = summary

                            } catch (e: JSONException) {
                                Log.e(TAG, "Exception: $e")
                            }
                        }

                    })
                }
            })

            ingredientPriceEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // Update the corresponding item in the list
                    ingredient.price = s.toString().toDouble()
                }
            })

            deleteIconImageView.setOnClickListener{
                val ingredientToDelete = ingredients[absoluteAdapterPosition]
//                Log.w(TAG, ingredientToDelete.toString())
                databaseReference.child("Ingredients").child(ingredientToDelete.id).removeValue()
            }
        }

    }
}