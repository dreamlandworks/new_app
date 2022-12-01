package com.satrango.ui.user.bookings.raise_ticket.models

data class RaiseTicketResModel(
    val complaint_id: Int,
    val message: String,
    val status: Int,
    val ticket_ref_id: String
)