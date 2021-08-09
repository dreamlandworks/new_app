package com.satrango.ui.user.bookings.booking_attachments.models

data class Addresses(
    val address_id: Int,
    val job_description: String,
    val sequence_no: Int,
    val weight_type: Int // 1 lite, 2 medium, 3 heavy
)