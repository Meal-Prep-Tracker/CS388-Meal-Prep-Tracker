package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

const val MEAL_EXTRA = "MEAL_EXTRA"
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
        View.OnClickListener, View.OnLongClickListener  {

        private val mealNameTextView = itemView.findViewById<TextView>(R.id.mealNameLbl)
        private val priceTextView = itemView.findViewById<TextView>(R.id.mealPriceLbl)
        private val caloriesTextView = itemView.findViewById<TextView>(R.id.mealCaloriesLbl)
        private val deleteIconImageView = itemView.findViewById<ImageView>(R.id.binIcon)
        init {
            itemView.setOnClickListener(this)
        }
        @SuppressLint("SetTextI18n")
        fun bind(meal: Meal) {
            mealNameTextView.text = meal.name
            priceTextView.text = "Price: $${meal.price}"
            caloriesTextView.text = "Calories: ${meal.calories}"
            deleteIconImageView.setOnClickListener{
                val mealToDelete = meals[absoluteAdapterPosition]
                Log.w(TAG, mealToDelete.toString())
                databaseReference.child("Meals").child(mealToDelete.id).removeValue()

                // Query to find ingredients with matching meal_id
                val deleteIngredientsQuery = databaseReference.child("Ingredients").orderByChild("meal_id").equalTo(mealToDelete.id)

                deleteIngredientsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
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
                })
            }
        }

        override fun onLongClick(v: View?): Boolean {
            // Get selected article
            val meal = meals[absoluteAdapterPosition]

//            // Navigate to Details screen and pass selected article
//            val intent = Intent(context, MealDetailActivity::class.java)
//            intent.putExtra(MEAL_EXTRA, meal)
//            context.startActivity(intent)
            Log.w(TAG, "Details of ${meal.toString()} will be shown...")
            return true
        }

        override fun onClick(v: View?) {
            // Get selected article
            val meal = meals[absoluteAdapterPosition]

            // Navigate to Details screen and pass selected article
            val intent = Intent(context, EditIngredientActivity::class.java)
            intent.putExtra(MEAL_EXTRA, meal)
            context.startActivity(intent)
        }

    }
}
