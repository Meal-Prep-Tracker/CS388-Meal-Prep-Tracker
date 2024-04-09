package com.example.mealpreptracker
import android.support.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NutritionSummary (
    @SerialName("calories")
    val calories: Double?  = 0.0,
    @SerialName("protein_g")
    val protein_g: Double?  = 0.0,
    @SerialName("carbohydrates_total_g")
    val carbohydrates_total_g: Double? = 0.0,
    @SerialName("fat_total_g")
    val fat_total_g: Double? = 0.0,
    @SerialName("fat_saturated_g")
    val fat_saturated_g: Double? = 0.0,
    @SerialName("sodium_mg")
    val sodium_mg: Double? = 0.0,
    @SerialName("potassium_mg")
    val potassium_mg: Double? = 0.0,
    @SerialName("cholesterol_mg")
    val cholesterol_mg: Double? = 0.0,
    @SerialName("fiber_g")
    val fiber_g: Double? = 0.0,
    @SerialName("sugar_g")
    val sugar_g: Double? = 0.0
):  java.io.Serializable

//@Keep
//@Serializable
//data class IngredientResponse(
//    @SerialName("response")
//    val response: BaseResponse?
//)
@Keep
@Serializable
data class BaseResponse(
    @SerialName("items")
    val items: List<NutritionSummary>
)

