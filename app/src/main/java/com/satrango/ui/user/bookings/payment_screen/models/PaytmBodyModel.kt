package com.satrango.ui.user.bookings.payment_screen.models

data class PaytmBodyModel(
    val requestType: String,
    val mid: String,
    val orderId: String,
    val paymentMode: String,
    val payerAccount: String
)