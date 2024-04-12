package com.example.mealpreptracker
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
data class Meal (
    val id: String = "",
    var user_id: String? = null, // TODO: this should not be null
    var name: String? = null,
    var servings: Int? = 0,
    var price: Double? = 0.0,
    val calories: Double? = 0.0,
    val date: Long? = 0L,
    var image_id: String? = null,
):  java.io.Serializable