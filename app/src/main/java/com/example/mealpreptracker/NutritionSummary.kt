package com.example.mealpreptracker
import android.support.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NutritionSummary (
    @SerialName("calories")
    val calories: Double?,
    @SerialName("protein_g")
    val protein: Double?,
    @SerialName("carbohydrates_total_g")
    val carbohydrates: Double?,
    @SerialName("fat_total_g")
    val fat: Double?,
    @SerialName("fat_saturated_g")
    val saturated_fat: Double?,
    @SerialName("sodium_mg")
    val sodium: Double?,
    @SerialName("potassium_mg")
    val potassium: Double?,
    @SerialName("cholesterol_mg")
    val cholesterol: Double?,
    @SerialName("fiber_g")
    val fiber: Double?,
    @SerialName("sugar_g")
    val sugar: Double?
):  java.io.Serializable
