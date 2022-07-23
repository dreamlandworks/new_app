package com.satrango.remote.fcm.models

data class SendFcmToAllReqModel(
    val notification: Notification,
    val priority: String,
    val to: List<To>
)