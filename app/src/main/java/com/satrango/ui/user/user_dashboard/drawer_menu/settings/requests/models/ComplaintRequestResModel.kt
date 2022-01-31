package com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests.models

data class ComplaintRequestResModel(
    val complaints: List<Complaint>,
    val message: String,
    val requests: List<Any>,
    val status: Int
)