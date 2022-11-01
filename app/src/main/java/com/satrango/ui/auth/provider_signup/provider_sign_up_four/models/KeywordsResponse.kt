package com.satrango.ui.auth.provider_signup.provider_sign_up_four.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class KeywordsResponse(
    @SerializedName("keyword_id")
    val keyword_id: String,
    @SerializedName("name")
    val name: String
): Serializable