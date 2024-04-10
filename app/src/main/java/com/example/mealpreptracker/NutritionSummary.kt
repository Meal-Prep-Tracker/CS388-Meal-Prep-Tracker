package com.example.mealpreptracker

import com.google.gson.annotations.SerializedName


data class NutritionSummary (
    @SerializedName("calories")
    val calories: Double?  = 0.0,
    @SerializedName("protein_g")
    val protein: Double?  = 0.0,
    @SerializedName("carbohydrates_total_g")
    val carbohydrates: Double? = 0.0,
    @SerializedName("fat_total_g")
    val fat: Double? = 0.0,
    @SerializedName("fat_saturated_g")
    val saturatedFat: Double? = 0.0,
    @SerializedName("sodium_mg")
    val sodium: Double? = 0.0,
    @SerializedName("potassium_mg")
    val potassium: Double? = 0.0,
    @SerializedName("cholesterol_mg")
    val cholesterol: Double? = 0.0,
    @SerializedName("fiber_g")
    val fiber: Double? = 0.0,
    @SerializedName("sugar_g")
    val sugar: Double? = 0.0
):  java.io.Serializable



