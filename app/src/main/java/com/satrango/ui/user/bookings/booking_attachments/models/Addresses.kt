package com.satrango.ui.user.bookings.booking_attachments.models

data class Addresses(
    val address_id: Int,
    val job_description: String,
    val sequence_no: Int,
    val weight_type: Int,
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val postal_code: String,
    val user_lat: String,
    val user_long: String,
)