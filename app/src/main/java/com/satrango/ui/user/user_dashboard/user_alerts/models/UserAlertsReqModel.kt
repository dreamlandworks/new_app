package com.satrango.ui.user.user_dashboard.user_alerts.models

data class UserAlertsReqModel(
    val id: String,
    val type: String,
    val key: String,
    val status: String
)