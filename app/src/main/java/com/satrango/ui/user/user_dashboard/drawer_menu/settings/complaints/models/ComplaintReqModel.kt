package com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models

data class ComplaintReqModel(
    val created_on: String,
    val description: String,
    val key: String,
    val module_id: Int,
    val users_id: String,
    val booking_id: Int,
    val user_type: String
)