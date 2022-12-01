package com.satrango.ui.user.bookings.booking_address.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class GetTxnReqModel(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("users_id")
    val users_id: String,
    @SerializedName("type")
    val type: String
): Serializable