package com.satrango.remote.fcm.models

import com.satrango.remote.Data

data class FCMMessageReqModel(
    val notification: Data,
    val priority: String,
    val to: String
)