package com.satrango.ui.auth.models.user_signup

data class UserLoginModel(
    val username: String,
    val password: String,
    val type: String
)