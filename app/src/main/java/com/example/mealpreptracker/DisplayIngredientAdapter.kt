package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DisplayIngredientAdapter(private val ingredients: List<Ingredient?>) : RecyclerView.Adapter<DisplayIngredientAdapter.ViewHolder>()  {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // TODO: Create member variables for any view that will be set
        // as you render a row.
        val ingredientNameTextView: TextView
        val ingredientQuantityTextView: TextView

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each sub-view
        init {
            // TODO: Store each of the layout's views into
            // the public final member variables created above
            ingredientNameTextView = itemView.findViewById(R.id.ingredientNameLbl)
            ingredientQuantityTextView = itemView.findViewById(R.id.ingredientAmtLbl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.item_mini_ingredient, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val ingredient = ingredients.get(position)
        // Set item views based on views and data model
        holder.ingredientNameTextView.text = ingredient?.name
        holder.ingredientQuantityTextView.text = "${ingredient?.quantity.toString()}g"
    }
}