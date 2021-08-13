package com.satrango.remote.fcm

import com.satrango.remote.Notification

data class FCMMessageReqModel(
    val notification: Notification,
    val priority: String,
    val to: String
)