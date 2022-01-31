package com.satrango.ui.user.bookings.view_booking_details.models

data class ProviderResponseReqModel(
    val amount: String,
    val booking_id: Int,
    val created_on: String,
    val description: String,
    val key: String,
    val sp_id: Int,
    val status_id: Int,
    val users_id: Int
)