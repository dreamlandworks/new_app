package com.satrango.ui.user.user_dashboard.search_service_providers.models

data class SearchServiceProviderResModel(
    val data: List<Data>,
    val message: String,
    val status: Int,
    val search_results_id: Int,
    val temp_address_id: Int
)