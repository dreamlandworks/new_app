package com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models

data class UserLocationChangeResModel(
    val `data`: List<Data>,
    val message: String,
    val status: Int,
    val temp_address_id: Int
)