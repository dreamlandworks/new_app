package com.satrango.ui.user.bookings.change_address

data class AddBookingAddressReqModel(
    val address: String,
    val apartment: String,
    val city: String,
    val country: String,
    val flat: String,
    val key: String,
    val landmark: String,
    val name: String,
    val postal_code: String,
    val state: String,
    val user_lat: String,
    val user_long: String,
    val users_id: Int
)