package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models

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
    val sp_fcm_token: String,
    val user_fcm_token: String,
    val mobile: String,
    val pause_status: String,
    val extra_demand_total_amount: String,
    val material_advance: String,
    val technician_charges: String,
    val expenditure_incurred: String,
    val otp: String,
    val profile_pic: String,
    val scheduled_date: String,
    val sp_id: String,
    val users_id: String,
    val started_at: String,
    val reschedule_id: String,
    val reschedule_status: String,
    val req_raised_by: String,
    val reschedule_date: String,
    val reschedule_time: String,
    val reschedule_description: String,
    val remaining_days_to_start: Int,
    val remaining_hours_to_start: Int,
    val remaining_minutes_to_start: Int,
    val time_slot_id: String
)