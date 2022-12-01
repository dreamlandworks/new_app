package com.satrango.ui.user.bookings.payment_screen.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class GetUserUpiReqModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("user_id")
    val user_id: Int
): Serializable