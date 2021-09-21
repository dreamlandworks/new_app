package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models

data class ProviderBookingResumeReqModel(
    val booking_id: Int,
    val key: String,
    val resumed_at: String,
    val sp_id: Int
)