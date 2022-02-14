package com.satrango.ui.user.user_dashboard.user_alerts.models

data class Action(
    val accept_response: String,
    val accept_text: String,
    val api: String,
    val created_on: String,
    val id: String,
    val reject_response: String,
    val reject_text: String,
    val text: String
)