package com.satrango.remote.fcm.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class To(
    @SerializedName("token")
    val token: String
): Serializable