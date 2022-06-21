package com.satrango.ui.user.bookings.raise_ticket.models

data class RaiseTicketReqModel(
    val booking_id: Int,
    val created_on: String,
    val description: String,
    val key: String,
    val module_id: Int,
    val user_type: Int,
    val users_id: String
)