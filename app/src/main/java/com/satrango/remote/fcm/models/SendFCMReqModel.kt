package com.satrango.remote.fcm.models

import com.satrango.remote.fcm.NotificationX

data class SendFCMReqModel(
    val notification: NotificationX,
    val priority: String,
    val to: String
)