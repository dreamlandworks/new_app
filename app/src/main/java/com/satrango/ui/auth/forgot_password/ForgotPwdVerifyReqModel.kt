package com.satrango.ui.auth.forgot_password

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ForgotPwdVerifyReqModel(
    @SerializedName("email")
    val email: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("mobile")
    val mobile: String
): Serializable