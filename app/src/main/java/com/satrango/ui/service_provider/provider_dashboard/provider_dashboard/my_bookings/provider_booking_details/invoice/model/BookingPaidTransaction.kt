package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.invoice.model

data class BookingPaidTransaction(
    val amount: String,
    val booking_id: String,
    val date: String,
    val payment_status: String,
    val reference_id: String,
    val transaction_method: String,
    val transaction_name: String,
    val transaction_type: String
)