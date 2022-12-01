package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderInvoiceResModel(
    @SerializedName("booking_details")
    val booking_details: BookingDetails,
    @SerializedName("booking_paid_transactions")
    val booking_paid_transactions: List<BookingPaidTransaction>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable