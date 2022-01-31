package com.satrango.ui.auth.user_signup.models

data class UserLoginModel(
    val username: String,
    val password: String,
    val type: String,
    val key: String
)