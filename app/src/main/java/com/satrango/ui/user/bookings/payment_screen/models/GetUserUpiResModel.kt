package com.satrango.ui.user.bookings.payment_screen.models

data class GetUserUpiResModel(
    val `data`: List<Data>,
    val message: String,
    val status: Int
)