package com.satrango.ui.auth.provider_signup.provider_sign_up_four.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class TimeslotResponse(
    @SerializedName("days")
    val days: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("to")
    val to: String
): Serializable