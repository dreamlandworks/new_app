package com.satrango.ui.auth.forgot_password

data class ForgotPwdVerifyReqModel(
    val email: String,
    val key: String,
    val mobile: String
)