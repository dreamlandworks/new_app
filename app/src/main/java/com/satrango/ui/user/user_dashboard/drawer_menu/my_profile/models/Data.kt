package com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models

data class Data(
    val address: List<Address>,
    val dob: String,
    val email_id: String,
    val fname: String,
    val lname: String,
    val mobile: String,
    val profile_pic: String,
    val referral_id: String
)