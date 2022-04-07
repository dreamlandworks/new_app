package com.satrango.ui.user.bookings.view_booking_details.models

data class CompleteBookingReqModel(
    val amount: String,
    val w_amount: String,
    val booking_id: String,
    val cgst: String,
    val completed_at: String,
    val key: String,
    val reference_id: String,
    val sgst: String,
    val sp_id: String,
    val total_amount: String,
    val users_id: String,
    val order_id: String
)