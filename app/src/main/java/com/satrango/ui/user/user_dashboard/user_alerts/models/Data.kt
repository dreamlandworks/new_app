package com.satrango.ui.user.user_dashboard.user_alerts.models

data class Data(
    val alert_type: String,
    val status: String,
    val description: String,
    val created_on: String,
    val booking_id: String,
    val reschedule_user_id: String,
    val reschedule_id: String,
    val alert_id: String,
    val action: String,
    val category_id: String,
    val post_job_id: String,
    val bid_id: String,
    val bid_sp_id: String,
    val id: String
)