package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews.models

data class SpReview(
    val behaviour: String,
    val booking_id: String,
    val feedback: String,
    val fname: String,
    val id: String,
    val lname: String,
    val mobile: String,
    val overall_rating: String,
    val created_dts: String,
    val professionalism: String,
    val profile_pic: String,
    val satisfaction: String,
    val skill: String,
    val sp_id: String
)