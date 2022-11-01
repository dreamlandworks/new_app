package com.satrango.ui.auth.provider_signup.provider_sign_up_four.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderIdProofReqModel(
    @SerializedName("id_proof")
    val id_proof: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("user_id")
    val user_id: String
): Serializable