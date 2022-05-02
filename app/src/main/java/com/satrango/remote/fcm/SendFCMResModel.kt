package com.satrango.remote.fcm

data class SendFCMResModel(
    val message: Message,
    val status: Int
)