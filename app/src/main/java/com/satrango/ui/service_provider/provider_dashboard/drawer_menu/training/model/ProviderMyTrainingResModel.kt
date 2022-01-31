package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model

data class ProviderMyTrainingResModel(
    val message: String,
    val points_earned: Int,
    val recent_videos: List<RecentVideo>,
    val recommended_videos: List<RecommendedVideo>,
    val status: Int,
    val total_points: Int,
    val total_videos: Int,
    val watched_videos: List<WatchedVideo>,
    val watched_videos_count: Int
)