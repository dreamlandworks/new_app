package com.satrango.ui.user.user_dashboard.user_alerts.models

data class Action(
    val accept_response: String,
    val accept_text: String,
    val api: String,
    val bid_id: Any,
    val bid_user_id: Any,
    val booking_id: String,
    val category_id: String,
    val created_on: String,
    val description: String,
    val id: String,
    val post_id: String,
    val profile_pic: String,
    val reject_response: String,
    val reject_text: String,
    val req_raised_by_id: String,
    val reschedule_id: String,
    val status: String,
    val status_code_id: String,
    val type_id: String,
    val updated_on: String,
    val user_id: String
)