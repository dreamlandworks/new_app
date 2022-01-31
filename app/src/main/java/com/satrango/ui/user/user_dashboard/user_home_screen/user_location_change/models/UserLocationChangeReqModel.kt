package com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models

data class UserLocationChangeReqModel(
    val address: String,
    val apartment: String,
    val city: String,
    val country: String,
    val flat: String,
    val key: String,
    val landmark: String,
    val name: String,
    val postal_code: String,
    val state: String,
    val user_lat: String,
    val user_long: String,
    val users_id: Int
)