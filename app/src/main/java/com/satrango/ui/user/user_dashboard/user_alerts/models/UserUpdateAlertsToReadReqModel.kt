package com.satrango.ui.user.user_dashboard.user_alerts.models

data class UserUpdateAlertsToReadReqModel(
    val id: String,
    val key: String,
    val user_type: String,
    val last_alert_id: String
)