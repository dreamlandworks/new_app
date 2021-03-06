package com.satrango.ui.user.user_dashboard.search_service_providers.models

data class SearchServiceProviderReqModel(
    val address: String,
    val city: String,
    val country: String,
    val key: String,
    val search_phrase_id: String,
    val search_phrase: String,
    val postal_code: String,
    val state: String,
    val user_lat: String,
    val user_long: String,
    val users_id: Int,
    val subcat_id: Int,
    val offer_id: Int
)