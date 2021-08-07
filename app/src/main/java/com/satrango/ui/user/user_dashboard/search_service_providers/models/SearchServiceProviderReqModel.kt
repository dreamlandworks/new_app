package com.satrango.ui.user.user_dashboard.search_service_providers.models

data class SearchServiceProviderReqModel(
    val address: String,
    val city: String,
    val country: String,
    val key: String,
    val keyword_id: Int,
    val postal_code: String,
    val state: String,
    val user_lat: String,
    val user_long: String,
    val users_id: Int,
    val subcat_id: Int
)