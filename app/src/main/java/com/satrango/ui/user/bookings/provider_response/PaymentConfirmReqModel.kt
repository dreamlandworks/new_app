package com.satrango.ui.user.bookings.provider_response

data class PaymentConfirmReqModel(
    val booking_amount: String,
    val booking_id: String,
    val date: String,
    val key: String,
    val payment_status: String,
    val reference_id: String,
    val sp_id: Int,
    val time_slot_from: String,
    val users_id: Int,
    val cgst: String,
    val sgst: String,
    val w_amount: String
)
