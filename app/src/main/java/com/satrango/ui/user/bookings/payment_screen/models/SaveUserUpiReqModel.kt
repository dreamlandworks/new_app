package com.satrango.ui.user.bookings.payment_screen.models

data class SaveUserUpiReqModel(
    val key: String,
    val upi_id: String,
    val user_id: Int
)