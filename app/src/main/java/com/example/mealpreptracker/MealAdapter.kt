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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

private const val TAG = "MealAdapter"
class MealAdapter(
    private val context: Context,
    private val meals: List<Meal>,
    private val databaseReference: DatabaseReference,
    private val storageRef: StorageReference
) : RecyclerView.Adapter<MealAdapter.ViewHolder>(){

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
            val servings = meal.servings
            // Make DB call here to set total pice and calories
            databaseReference.child("Ingredients").orderByChild("meal_id").equalTo(meal.id).get()
                .addOnSuccessListener { snapshot ->
                    // Get the ingredients of the meal u pass from Main Activity in your Intent
                    val ingredients = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Ingredient::class.java)
                    }
                    Log.w(TAG, "Setting total cals and price of meal")

//                    ingredients.map { ingredient -> ingredient?.nutritionSummary?.calories ?: 0.0 }

                    val calories = (ingredients.sumOf { it?.nutritionSummary?.calories ?: 0.0 } / servings!!).toFloat()
                    val roundedCalories = "%.1f".format(calories)

                    val totalPrice = "%.2f".format(ingredients.sumOf { it?.price ?: 0.0 })

                    priceTextView.text = "Total Price: \$${totalPrice} for $servings servings"
                    caloriesTextView.text = "Calories (per serving): $roundedCalories"

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

                // Delete all ingredients for this meal
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

                // Delete the image for this meal
                mealToDelete.image_id?.let { it1 -> storageRef.child(it1).delete()
                    .addOnSuccessListener {
                    Log.w("firebase", "File deleted!")
                }
                    .addOnFailureListener {
                    Log.w("firebase", "Error occured while deleting file") }
                }

            }
        }

        override fun onLongClick(v: View?): Boolean {
            // Get selected article
            val meal = meals[absoluteAdapterPosition]

            // Navigate to Meal Details screen and pass selected meal and its ingredients
            val intent = Intent(context, EditMealActivity::class.java)
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

