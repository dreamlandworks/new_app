package com.satrango.ui.user.bookings.booking_address.models

data class GetTxnReqModel(
    val amount: String,
    val key: String,
    val users_id: String,
    val type: String
)