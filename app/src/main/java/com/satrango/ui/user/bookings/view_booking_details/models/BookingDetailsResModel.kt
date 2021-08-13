package com.satrango.ui.user.bookings.view_booking_details.models

data class BookingDetailsResModel(
    val attachments: List<Attachment>,
    val booking_details: BookingDetails,
    val job_details: List<JobDetail>,
    val message: String,
    val status: Int
)