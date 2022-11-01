package com.satrango.ui.user.bookings.provider_response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class PaymentConfirmReqModel(
    @SerializedName("booking_amount")
    val booking_amount: String,
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("sp_id")
    val sp_id: Int,
    @SerializedName("time_slot_from")
    val time_slot_from: String,
    @SerializedName("users_id")
    val users_id: Int,
    @SerializedName("cgst")
    val cgst: String,
    @SerializedName("sgst")
    val sgst: String,
    @SerializedName("order_id")
    val order_id: String,
    @SerializedName("w_amount")
    val w_amount: String
): Serializable
