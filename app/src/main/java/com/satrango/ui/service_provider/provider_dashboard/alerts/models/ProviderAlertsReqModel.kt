package com.satrango.ui.service_provider.provider_dashboard.alerts.models

data class ProviderAlertsReqModel(
    val key: String,
    val sp_id: String,
    val status: String,
    val type: String
)