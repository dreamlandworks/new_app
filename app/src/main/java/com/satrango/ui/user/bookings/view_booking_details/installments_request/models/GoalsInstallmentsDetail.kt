package com.satrango.ui.user.bookings.view_booking_details.installments_request.models

data class GoalsInstallmentsDetail(
    val amount: String,
    val booking_id: String,
    val created_dts: String,
    val status_id: String,
    val goal_id: String,
    val id: String,
    val inst_no: String,
    val inst_paid_status: String,
    val inst_request_status_id: String,
    val post_job_id: String,
    val transaction_id: String
)