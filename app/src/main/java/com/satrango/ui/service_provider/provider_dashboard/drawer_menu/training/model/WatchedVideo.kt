package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class WatchedVideo(
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("points")
    val points: String,
    @SerializedName("subcategories_id")
    val subcategories_id: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("video_categories_id")
    val video_categories_id: String
): Serializable