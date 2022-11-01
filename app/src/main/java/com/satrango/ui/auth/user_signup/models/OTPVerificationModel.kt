package com.satrango.ui.auth.user_signup.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class OTPVerificationModel(
    @SerializedName("fname")
    val fname: String,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("key")
    val key: String
): Serializable