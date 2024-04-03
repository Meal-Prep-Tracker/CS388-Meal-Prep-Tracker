package com.example.mealpreptracker
import android.support.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.serialization.Serializable
//@Keep
//@Serializable
@IgnoreExtraProperties
data class Meal (
//    val id: Int,
//    val user_id: String?,
    val name: String? = null,
    val servings: Int? = null,
    val price: Double? = 0.0,
    val calories: Double? = 0.0,
    val nutritionSummary: NutritionSummary?  = null
):  java.io.Serializable