package com.satrango.ui.auth.user_signup.models

data class OTPVerificationModel(
    val fname: String,
    val lname: String,
    val mobile: String,
    val key: String
)