package com.satrango.ui.user.bookings.booking_address.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class GetTxnResModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("order_id")
    val order_id: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("txn_id")
    val txn_id: String
): Serializable