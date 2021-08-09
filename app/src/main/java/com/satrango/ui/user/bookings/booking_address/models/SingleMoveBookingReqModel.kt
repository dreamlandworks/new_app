package com.satrango.ui.user.bookings.booking_address.models

data class SingleMoveBookingReqModel(
    val address_id: Int,
    val amount: String,
    val attachments: List<Attachment>,
    val created_on: String,
    val estimate_time: Int,
    val estimate_type_id: Int,
    val job_description: String,
    val key: String,
    val scheduled_date: String,
    val sp_id: Int,
    val started_at: String,
    val temp_address_id: Int,
    val time_slot_from: String,
    val time_slot_to: String,
    val users_id: Int
)