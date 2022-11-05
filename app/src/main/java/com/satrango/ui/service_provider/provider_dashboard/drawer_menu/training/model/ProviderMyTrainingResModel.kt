package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderMyTrainingResModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("points_earned")
    val points_earned: Int,
    @SerializedName("recent_videos")
    val recent_videos: List<RecentVideo>,
    @SerializedName("recommended_videos")
    val recommended_videos: List<RecommendedVideo>,
    @SerializedName("status")
    val status: Int,
    @SerializedName("total_points")
    val total_points: Int,
    @SerializedName("total_videos")
    val total_videos: Int,
    @SerializedName("watched_videos")
    val watched_videos: List<WatchedVideo>,
    @SerializedName("watched_videos_count")
    val watched_videos_count: Int
): Serializable