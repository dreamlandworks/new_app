package com.satrango.ui.user.bookings.payment_screen.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class SaveUserUpiReqModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("upi_id")
    val upi_id: String,
    @SerializedName("user_id")
    val user_id: Int
): Serializable