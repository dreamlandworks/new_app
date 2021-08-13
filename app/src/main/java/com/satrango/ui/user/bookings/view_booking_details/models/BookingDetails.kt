package com.satrango.ui.user.bookings.view_booking_details.models

data class BookingDetails(
    val amount: String,
    val estimate_time: String,
    val estimate_type: String,
    val fcm_token: String,
    val fname: String,
    val from: String,
    val lname: String,
    val mobile: String,
    val scheduled_date: String,
    val started_at: String,
    val sp_id: String
)