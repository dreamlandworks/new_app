package com.satrango.remote.fcm.models

import com.satrango.remote.fcm.Message

data class SendFCMResModel(
    val message: Message,
    val status: Int
)