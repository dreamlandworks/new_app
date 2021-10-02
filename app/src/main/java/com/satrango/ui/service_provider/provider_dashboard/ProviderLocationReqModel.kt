package com.satrango.ui.service_provider.provider_dashboard

data class ProviderLocationReqModel(
    val address: String,
    val city: String,
    val country: String,
    val key: String,
    val online_status_id: Int,
    val postal_code: String,
    val state: String,
    val user_lat: String,
    val user_long: String,
    val users_id: Int
)