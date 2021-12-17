package com.satrango.ui.service_provider.provider_dashboard.dashboard.models

data class ProviderDashboardResModel(
    val bids_awarded: Int,
    val bookings_completed: String,
    val competitor_name: String,
    val competitor_rank: Int,
    val earnings: String,
    val commission: String,
    val message: String,
    val sp_points: Int,
    val sp_rank: Int,
    val sp_rating: Int,
    val status: Int,
    val total_bids: String,
    val total_bookings: String
)