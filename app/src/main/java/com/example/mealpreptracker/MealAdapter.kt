package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.Query
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import java.util.ArrayList

private const val TAG = "MealAdapter"
class MealAdapter(private val context: Context, private val meals: List<Meal>, private val databaseReference: DatabaseReference) : RecyclerView.Adapter<MealAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meal = meals[position]
        holder.bind(meal)
    }

    override fun getItemCount() = meals.size
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {

        private val mealNameTextView = itemView.findViewById<TextView>(R.id.mealNameLbl)
        private val priceTextView = itemView.findViewById<TextView>(R.id.mealPriceLbl)
        private val caloriesTextView = itemView.findViewById<TextView>(R.id.mealCaloriesLbl)
        private val deleteIconImageView = itemView.findViewById<ImageView>(R.id.binIcon)
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
        @SuppressLint("SetTextI18n")
        fun bind(meal: Meal) {
            mealNameTextView.text = meal.name
            // Make DB call here to set total pice and calories
            databaseReference.child("Ingredients").orderByChild("meal_id").equalTo(meal.id).get()
                .addOnSuccessListener { snapshot ->
                    // Get the ingredients of the meal u pass from Main Activity in your Intent
                    val ingredients = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Ingredient::class.java)
                    }
                    Log.w(TAG, "Setting total cals and price of meal")

                    priceTextView.text = "Price: \$${ingredients.sumOf { it?.price ?: 0.0 }}"
                    caloriesTextView.text = "Calories: ${ingredients.sumOf { it?.nutritionSummary?.calories ?: 0.0 }}"

                }
                .addOnFailureListener {
                    Log.e(TAG, "Error getting total cals and price of meal")
                }

            deleteIconImageView.setOnClickListener{
                val mealToDelete = meals[absoluteAdapterPosition]
                Log.w(TAG, mealToDelete.toString())

                // Delete the single meal from the Meals collection
                databaseReference.child("Meals").child(mealToDelete.id).removeValue()

                // Query to find ingredients with matching meal_id
                val deleteIngredientsQuery = databaseReference.child("Ingredients").orderByChild("meal_id").equalTo(mealToDelete.id)

                deleteIngredientsQuery.get()
                    .addOnSuccessListener {
                    snapshot -> snapshot.children.forEach {
                        childSnapshot -> childSnapshot.ref.removeValue()
                    }
                        Log.w(TAG, "Removed all ingredients sucessfully for meal ${mealToDelete.id}")
                }
                    .addOnFailureListener {
                        Log.e("firebase", "Error deleteing ingredients", it)
                    }

                /* deleteIngredientsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Loop through the results and delete each node
                        for (ingredientSnapshot in dataSnapshot.children){
                            ingredientSnapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    println("Ingredient deleted successfully.")
                                }
                                .addOnFailureListener { error ->
                                    println("Error deleting ingredient: $error")
                                }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        println("Error querying ingredients: $databaseError")
                    }
                }) */
            }
        }

        override fun onLongClick(v: View?): Boolean {
            // Get selected article
            val meal = meals[absoluteAdapterPosition]

            // Navigate to Meal Details screen and pass selected meal and its ingredients
            val intent = Intent(context, EditIngredientActivity::class.java)
            intent.putExtra(MEAL_EXTRA, meal)
            context.startActivity(intent)
            return true
        }

        override fun onClick(v: View?) {
            // Get selected article
            val meal = meals[absoluteAdapterPosition]

            val intent = Intent(context, MealDetailActivity::class.java)
            intent.putExtra(MEAL_EXTRA, meal)
            context.startActivity(intent)
        }

    }
}

