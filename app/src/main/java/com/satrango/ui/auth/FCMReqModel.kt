package com.satrango.ui.auth

data class FCMReqModel(
    val fcm_token: String,
    val key: String,
    val user_id: String
)