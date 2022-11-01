package com.satrango.ui.auth.provider_signup.provider_sign_up_two.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProviderSignUpTwoKeywordsResModel(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)