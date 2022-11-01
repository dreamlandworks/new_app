package com.satrango.ui.auth.provider_signup.provider_sign_up_four.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class QualificationResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("qual_id")
    val qual_id: String
): Serializable