package com.satrango.ui.user.bookings.provider_response

data class PaymentConfirmReqModel(
    val amount: String,
    val booking_id: String,
    val date: String,
    val key: String,
    val payment_status: String,
    val reference_id: String,
    val sp_id: Int,
    val time_slot_from: String,
    val users_id: Int
)