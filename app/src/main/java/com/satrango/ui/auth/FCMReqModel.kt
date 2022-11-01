package com.satrango.ui.auth

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class FCMReqModel(
    @SerializedName("fcm_token")
    val fcm_token: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("user_id")
    val user_id: String
): Serializable