package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models

data class MyBookingsResModel(
    val booking_details: List<BookingDetail>,
    val message: String,
    val status: Int
)