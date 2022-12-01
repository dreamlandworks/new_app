package com.satrango.ui.auth.login_screen

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class LogoutReqModel(
    @SerializedName("user_id")
    val user_id: Int,
    @SerializedName("key")
    val key: String
): Serializable
