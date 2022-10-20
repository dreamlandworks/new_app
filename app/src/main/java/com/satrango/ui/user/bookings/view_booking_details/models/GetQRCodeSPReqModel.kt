package com.satrango.ui.user.bookings.view_booking_details.models

data class GetQRCodeSPReqModel(
    val address: String,
    val cat_id: Int,
    val city: String,
    val country: String,
    val key: String,
    val offer_id: Int,
    val postal_code: String,
    val profession_id: Int,
    val sp_id: Int,
    val state: String,
    val user_lat: String,
    val user_long: String,
    val users_id: Int
)