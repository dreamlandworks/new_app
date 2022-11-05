package com.satrango.remote.fcm.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SendFcmToAllReqModel(
    @SerializedName("notification")
    val notification: Notification,
    @SerializedName("priority")
    val priority: String,
    @SerializedName("to")
    val to: List<To>
)