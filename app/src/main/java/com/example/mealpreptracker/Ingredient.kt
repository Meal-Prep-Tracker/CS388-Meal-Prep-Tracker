package com.example.mealpreptracker
import android.support.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Ingredient (
    val id: String = "",
    val meal_id: String,
    val name: String? = null,
    val quantity: Double? = null,
    val price: Double? = null,
    val nutritionSummary: NutritionSummary? = null
):  java.io.Serializable
