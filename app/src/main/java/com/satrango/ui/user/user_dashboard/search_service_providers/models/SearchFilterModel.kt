package com.satrango.ui.user.user_dashboard.search_service_providers.models

data class SearchFilterModel(
    val rating: Boolean,
    val ranking: Boolean,
    val nearMe: Boolean,
    val lowToHigh: Boolean,
    val highToLow: Boolean,
    val fresher: Boolean,
    val experience: Boolean,
    val any: Boolean,
    val distance: String,
    val priceRangeFrom: String,
    val priceRangeTo: String

)
