package com.satrango.ui.user.bookings.view_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class CompleteBookingReqModel(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("w_amount")
    val w_amount: String,
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("cgst")
    val cgst: String,
    @SerializedName("completed_at")
    val completed_at: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("reference_id")
    val reference_id: String,
    @SerializedName("sgst")
    val sgst: String,
    @SerializedName("sp_id")
    val sp_id: String,
    @SerializedName("total_amount")
    val total_amount: String,
    @SerializedName("users_id")
    val users_id: String,
    @SerializedName("order_id")
    val order_id: String,
    @SerializedName("expenditure_incurred")
    val expenditure_incurred: String,
): Serializable