package com.satrango.ui.user.user_dashboard.user_alerts.models

data class UserAlertsResModel(
    val `data`: List<Data>,
    val message: String,
    val status: Int
)