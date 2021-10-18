package com.satrango.ui.user.bookings.view_booking_details.models

data class RescheduleBookingReqModel(
    val booking_id: Int,
    val key: String,
    val rescheduled_date: String,
    val rescheduled_time_slot_from: String,
    val scheduled_date: String,
    val scheduled_time_slot_id: Int,
    val users_id: Int,
    val user_type: String
)