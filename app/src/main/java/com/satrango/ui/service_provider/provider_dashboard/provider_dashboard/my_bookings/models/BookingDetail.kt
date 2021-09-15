package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models

data class BookingDetail(
    val amount: String,
    val booking_id: String,
    val booking_status: String,
    val category_id: String,
    val details: List<Detail>,
    val estimate_time: String,
    val estimate_type: String,
    val fname: String,
    val from: String,
    val lname: String,
    val mobile: String,
    val otp: String,
    val profile_pic: String,
    val scheduled_date: String,
    val sp_id: String,
    val users_id: String,
    val started_at: String,
    val time_slot_id: String
)