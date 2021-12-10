package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review

data class ProviderRatingReqModel(
    val overall_rating: Float,
    val professionalism: Float,
    val skill: Float,
    val behaviour: Float,
    val satisfaction: Float,
    val feedback: String,
    val booking_id: Int,
    val sp_id: Int,
    val key: String
)