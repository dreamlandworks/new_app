package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models

data class BookingStatusDetail(
    val booking_id: String,
    val booking_status_id: String,
    val created_on: String,
    val description: Any,
    val id: String,
    val name: String,
    val sp_id: String,
    val status_id: String
)