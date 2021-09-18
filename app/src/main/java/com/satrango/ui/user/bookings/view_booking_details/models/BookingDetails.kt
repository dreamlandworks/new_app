package com.satrango.ui.user.bookings.view_booking_details.models

data class BookingDetails(
    val amount: String,
    val estimate_time: String,
    val estimate_type: String,
    val fcm_token: String,
    val fname: String,
    val from: String,
    val time_slot_id: String,
    val lname: String,
    val mobile: String,
    val post_job_id: String,
    val extra_demand_status: String,
    val extra_demand_total_amount: Double,
    val material_advance: Double,
    val technician_charges: Double,
    val expenditure_incurred: Double,
    val scheduled_date: String,
    val started_at: String,
    val sp_id: String,
    val otp: String
)