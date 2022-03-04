package com.satrango.ui.service_provider.provider_dashboard.alerts.models

data class UpdateAlertsToReadReqModel(
    val id: String,
    val key: String,
    val user_type: String,
    val last_alert_id: String
)