package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models

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
    val extra_demand_total_amount: Double,
    val material_advance: Double,
    val technician_charges: Double,
    val expenditure_incurred: Double,
    val time_slot_id: String,
    val profile_pic: String,
    val sp_fcm_token: String,
    val user_fcm_token: String,
    val lname: String,
    val mobile: String,
    val scheduled_date: String,
    val sp_id: String,
    val started_at: String,
    val otp: String
)