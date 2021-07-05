package com.satrango.ui.user_dashboard.drawer_menu.my_profile.models

data class UserProfileUpdateReqModel(
    val dob: String,
    val email: String,
    val fname: String,
    val image: String,
    val lname: String,
    val user_id: String,
    val key: String
)