package com.example.mealpreptracker

import android.support.annotation.Keep
import kotlinx.serialization.Serializable


@Keep
@Serializable
data class User(
    val uid: String = "",
    var firstName: String? = null,
    var lastName: String? = null,
    val email: String? = null
):  java.io.Serializable
