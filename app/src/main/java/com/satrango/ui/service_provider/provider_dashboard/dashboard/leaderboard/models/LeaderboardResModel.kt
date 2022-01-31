package com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.models

data class LeaderboardResModel(
    val data: List<Data>,
    val message: String,
    val sp_data: SpData,
    val status: Int
)