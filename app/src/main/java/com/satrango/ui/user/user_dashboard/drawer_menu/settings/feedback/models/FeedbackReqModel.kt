package com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models

data class FeedbackReqModel(
    val created_on: String,
    val description: String,
    val key: String,
    val users_id: String,
    val user_type: String
)