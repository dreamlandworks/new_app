package com.satrango.ui.user.bookings.view_booking_details.models

data class BookingDetailsReqModel(
    val booking_id: Int,
    val category_id: Int,
    val key: String,
    val users_id: Int
)