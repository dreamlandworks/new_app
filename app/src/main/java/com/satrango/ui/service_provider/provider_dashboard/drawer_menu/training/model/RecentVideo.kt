package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model

data class RecentVideo(
    val created_on: String,
    val description: String,
    val id: String,
    val name: String,
    val points: String,
    val subcategories_id: String,
    val url: String,
    val video_categories_id: String
)