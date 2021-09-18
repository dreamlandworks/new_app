package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.release_goals.models

data class ProviderPostRequestInstallmentResModel(
    val message: String,
    val status: Int,
    val user_fcm_token: String
)