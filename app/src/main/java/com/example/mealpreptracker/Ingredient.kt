package com.example.mealpreptracker
import android.support.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Ingredient (
    val id: String = "",
    val meal_id: String,
    var name: String? = null,
    var quantity: Double? = null,
    var price: Double? = null,
    var nutritionSummary: NutritionSummary? = null
):  java.io.Serializable
