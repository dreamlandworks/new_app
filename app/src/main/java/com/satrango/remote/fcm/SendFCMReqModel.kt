package com.satrango.remote.fcm

data class SendFCMReqModel(
    val notification: NotificationX,
    val priority: String,
    val to: String
)