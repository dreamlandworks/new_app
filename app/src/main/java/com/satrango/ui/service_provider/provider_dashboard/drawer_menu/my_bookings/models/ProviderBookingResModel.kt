package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models

data class ProviderBookingResModel(
    val booking_details: List<BookingDetail>,
    val message: String,
    val status: Int
)