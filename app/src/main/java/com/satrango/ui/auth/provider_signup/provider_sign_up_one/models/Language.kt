package com.satrango.ui.auth.provider_signup.provider_sign_up_one.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Language(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
): Serializable