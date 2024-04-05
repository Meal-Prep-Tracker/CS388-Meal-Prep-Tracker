package com.example.mealpreptracker;

import android.app.Activity;
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_detail)

        // TODO: Find the views for the screen
        mealNameHeaderTextView = findViewById(R.id.mealName)

        // TODO: Get the extra from the Intent

        // TODO: Set the mealNameHeader, meal details, and nutrition summary of the meal

        // TODO: setup the Recycler View, Get the ingredients of the meal u pass from Main Activity in your Intent
    }


}
