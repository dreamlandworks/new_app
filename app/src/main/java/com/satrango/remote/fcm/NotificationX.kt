package com.satrango.remote.fcm

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class NotificationX(
    @SerializedName("body")
    val body: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("title")
    val title: String
): Serializable