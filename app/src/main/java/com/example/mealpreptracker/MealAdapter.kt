package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

const val MEAL_EXTRA = "MEAL_EXTRA"
private const val TAG = "MealAdapter"
class MealAdapter(private val context: Context, private val meals: List<Meal>) : RecyclerView.Adapter<MealAdapter.ViewHolder>() {

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
        View.OnClickListener {

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
                TODO("run a delete query on Meal and Meal Ingredients")
            }
        }

        override fun onClick(v: View?) {
            // Get selected article
            val meal = meals[absoluteAdapterPosition]

            // Navigate to Details screen and pass selected article
//            val intent = Intent(context, DetailActivity::class.java)
//            intent.putExtra(MEAL_EXTRA, meal)
//            context.startActivity(intent)
        }

    }
}
