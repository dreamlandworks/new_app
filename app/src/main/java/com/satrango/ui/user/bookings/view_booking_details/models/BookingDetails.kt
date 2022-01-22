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
    val extra_demand_total_amount: String,
    val material_advance: String,
    val technician_charges: String,
    val expenditure_incurred: String,
    val scheduled_date: String,
    val started_at: String,
    val sp_id: String,
    val reschedule_status: String,
    val otp: String,
    val otp_raised_by: String
)