package com.satrango.ui.user.user_dashboard.user_alerts.models

data class UserAlertsResModelX(
    val action: List<Action>,
    val message: String,
    val regular: List<Regular>,
    val status: Int
)