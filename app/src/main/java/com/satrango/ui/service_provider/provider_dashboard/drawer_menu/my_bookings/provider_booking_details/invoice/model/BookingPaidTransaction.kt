package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BookingPaidTransaction(
    @SerializedName("date")
    val date: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("reference_id")
    val reference_id: String,
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("payment_status")
    val payment_status: String,
    @SerializedName("transaction_name")
    val transaction_name: String,
    @SerializedName("transaction_method")
    val transaction_method: String,
    @SerializedName("transaction_type")
    val transaction_type: String,
    @SerializedName("final_dues")
    val final_dues: String
): Serializable