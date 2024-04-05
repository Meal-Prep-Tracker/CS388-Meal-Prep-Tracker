package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference

private const val TAG = "IngredientAdapter"
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
            deleteIconImageView.setOnClickListener{
                val ingredientToDelete = ingredients[absoluteAdapterPosition]
//                Log.w(TAG, ingredientToDelete.toString())
                databaseReference.child("Ingredients").child(ingredientToDelete.id).removeValue()
            }
        }

    }
}