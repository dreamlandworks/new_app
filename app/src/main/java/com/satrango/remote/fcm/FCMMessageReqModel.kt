package com.satrango.remote.fcm

import com.satrango.remote.Data

data class FCMMessageReqModel(
    val notification: Data,
    val priority: String,
    val to: String
)