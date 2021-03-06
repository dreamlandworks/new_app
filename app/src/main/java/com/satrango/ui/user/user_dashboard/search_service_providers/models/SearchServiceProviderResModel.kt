package com.satrango.ui.user.user_dashboard.search_service_providers.models

data class SearchServiceProviderResModel(
    val charges: List<Charge>,
    val data: List<Data>,
    val message: String,
    val wallet_balance: Int,
    val search_results_id: Int,
    val slots_data: List<SlotsData>,
    val status: Int,
    val temp_address_id: Int
)