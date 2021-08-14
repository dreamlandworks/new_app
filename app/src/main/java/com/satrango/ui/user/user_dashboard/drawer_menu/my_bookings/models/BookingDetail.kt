package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models

data class BookingDetail(
    val amount: String,
    val booking_id: String,
    val category_id: String,
    val estimate_time: String,
    val estimate_type: String,
    val fname: String,
    val from: String,
    val lname: String,
    val mobile: String,
    val scheduled_date: String,
    val sp_id: String,
    val started_at: String,
    val booking_status: String
)