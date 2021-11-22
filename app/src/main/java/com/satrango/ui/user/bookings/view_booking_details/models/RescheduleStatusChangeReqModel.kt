package com.satrango.ui.user.bookings.view_booking_details.models

data class RescheduleStatusChangeReqModel(
    val booking_id: Int,
    val key: String,
    val reschedule_id: Int,
    val sp_id: Int,
    val status_id: Int,
    val user_type: String,
    val users_id: Int
)