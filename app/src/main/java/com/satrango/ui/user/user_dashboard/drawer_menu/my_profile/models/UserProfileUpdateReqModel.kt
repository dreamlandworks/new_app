package com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserProfileUpdateReqModel(
    @SerializedName("dob")
    val dob: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("fname")
    val fname: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("user_id")
    val user_id: String,
    @SerializedName("key")
    val key: String
): Serializable