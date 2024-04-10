package com.example.mealpreptracker

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class EditMealActivity : AppCompatActivity() {
    private lateinit var mealName: EditText;
    private lateinit var servings: EditText;
    private lateinit var image: ImageButton;

    private val MEAL_ID = "meal_id";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_meal)

        mealName = findViewById(R.id.meal_name_input);
        servings = findViewById(R.id.servings_input)

        intent.getSerializableExtra(MEAL_ID) as Meal;
    }
}