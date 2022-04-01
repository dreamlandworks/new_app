package com.satrango.ui.user.bookings.payment_screen.models

data class PaytmProcessTxnReqModel(
    val amount: String,
    val booking_id: String,
    val txn_token: String,
    val users_id: String
)