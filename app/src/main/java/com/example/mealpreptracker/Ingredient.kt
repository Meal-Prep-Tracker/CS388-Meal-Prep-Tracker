package com.example.mealpreptracker
import android.support.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Ingredient (
    val id: String = "",
    val meal_id: String? = null,
    var name: String? = null,
    var quantity: Int? = null,
    var price: Double? = null,
    var nutritionSummary: NutritionSummary? = null
):  java.io.Serializable
