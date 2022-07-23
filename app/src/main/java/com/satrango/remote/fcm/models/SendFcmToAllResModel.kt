package com.satrango.remote.fcm.models

data class SendFcmToAllResModel(
    val message: List<Message>,
    val status: Int
)