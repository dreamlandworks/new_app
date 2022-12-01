package com.satrango.remote.fcm.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.remote.fcm.NotificationX
import java.io.Serializable

@Keep
data class SendFCMReqModel(
    @SerializedName("notification")
    val notification: NotificationX,
    @SerializedName("priority")
    val priority: String,
    @SerializedName("to")
    val to: String
): Serializable