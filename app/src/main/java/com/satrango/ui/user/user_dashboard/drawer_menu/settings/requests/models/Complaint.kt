package com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests.models

data class Complaint(
    val description: String,
    val id: String,
    val replies: List<Reply>,
    val status: String
)