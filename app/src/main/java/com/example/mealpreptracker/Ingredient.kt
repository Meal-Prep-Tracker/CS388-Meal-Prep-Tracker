package com.example.mealpreptracker
import android.support.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Ingredient (
//    val meal_id: String? = null,
    val name: String?,
    val quantity: Double? = null,
    val price: Double? = null,
    val calories: Double? = null,
    val nutritionSummary: NutritionSummary? = null
):  java.io.Serializable
