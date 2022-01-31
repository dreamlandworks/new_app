package com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models

data class Data(
    val address: List<Address>,
    val dob: String,
    val email_id: String,
    val fname: String,
    val lname: String,
    val gender: String,
    val mobile: String,
    val profile_pic: String,
    val referral_id: String,
    val sp_activated: String, // 1 = Not Activated, 2 = Approval Waiting, 3 = Activated, 4 = Not Approved, 5 = Banned
    val activation_code: String // 0 = Not Applied Yet, 1 = ID Card Uploaded, 2 = 1st Video Uploaded, 3 = 2nd Video Uploaded , 4 = 3rd Video Uploaded, 5 = Activated
)