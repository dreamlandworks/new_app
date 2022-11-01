package com.satrango.ui.auth.user_signup.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserLoginModel(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("key")
    val key: String
): Serializable