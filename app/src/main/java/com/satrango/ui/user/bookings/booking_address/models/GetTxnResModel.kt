package com.satrango.ui.user.bookings.booking_address.models

data class GetTxnResModel(
    val message: String,
    val order_id: String,
    val status: Int,
    val txn_id: String
)