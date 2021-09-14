package com.satrango.ui.user.bookings.view_booking_details.models

data class RescheduleBookingResModel(
    val booking_id: Int,
    val message: String,
    val reschedule_id: Int,
    val status: Int
)