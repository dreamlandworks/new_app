package com.satrango.ui.auth.user_signup.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserResetPwdModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("key")
    val key: String
): Serializable
