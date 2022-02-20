package com.satrango.ui.service_provider.provider_dashboard.dashboard.models

data class ProviderDashboardResModel(
    val bids_awarded: String,
    val bookings_completed: String,
    val competitor_name: String,
    val competitor_rank: String,
    val earnings: String,
    val commission: String,
    val message: String,
    val sp_points: String,
    val sp_rank: String,
    val sp_rating: String,
    val status: Int,
    val total_bids: String,
    val total_bookings: String
)