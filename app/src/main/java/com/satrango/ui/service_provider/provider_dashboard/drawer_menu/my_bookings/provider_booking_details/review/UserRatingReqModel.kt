package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review

data class UserRatingReqModel(
    val app_review: Float,
    val booking_id: Int,
    val booking_rating: Float,
    val feedback: String,
    val job_satisfaction: Float,
    val key: String,
    val overall_rating: Float,
    val user_id: Int,
    val user_rating: Float
)