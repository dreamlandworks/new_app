package com.satrango.ui.user.bookings.cancel_booking.models

data class UserBookingCancelReqModel(
    val booking_id: Int,
    val cancelled_by: Int,
    val key: String,
    val reasons: String,
    val status_id: Int
)