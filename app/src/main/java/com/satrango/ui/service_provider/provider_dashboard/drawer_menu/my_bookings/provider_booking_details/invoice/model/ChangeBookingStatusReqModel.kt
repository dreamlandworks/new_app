package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

data class ChangeBookingStatusReqModel(
    val booking_id: String,
    val key: String,
    val sp_id: String,
    val status_id: String
)