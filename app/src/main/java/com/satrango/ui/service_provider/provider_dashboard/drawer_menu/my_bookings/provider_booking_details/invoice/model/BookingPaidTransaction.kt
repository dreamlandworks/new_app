package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

data class BookingPaidTransaction(
    val date: String,
    val amount: String,
    val reference_id: String,
    val booking_id: String,
    val payment_status: String,
    val transaction_name: String,
    val transaction_method: String,
    val transaction_type: String,
    val final_dues: String
)