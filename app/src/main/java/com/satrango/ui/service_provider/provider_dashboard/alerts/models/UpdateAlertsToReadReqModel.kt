package com.satrango.ui.service_provider.provider_dashboard.alerts.models

data class UpdateAlertsToReadReqModel(
    val id: String,
    val key: String,
    val status: String,
    val type: String
)