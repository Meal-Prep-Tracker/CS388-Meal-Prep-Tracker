package com.example.mealpreptracker
import android.support.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.serialization.Serializable
//@Keep
//@Serializable
@IgnoreExtraProperties
data class Meal (
    val id: String = "",
//    val user_id: String?,
    val name: String? = null,
    val servings: Int? = 0,
    val price: Double? = 0.0,
    val calories: Double? = 0.0,
    val date: Long? = 0L
):  java.io.Serializable