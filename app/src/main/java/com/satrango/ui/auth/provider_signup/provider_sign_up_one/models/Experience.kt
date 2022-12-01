package com.satrango.ui.auth.provider_signup.provider_sign_up_one.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Experience(
    @SerializedName("exp")
    val exp: String,
    @SerializedName("id")
    val id: String
)