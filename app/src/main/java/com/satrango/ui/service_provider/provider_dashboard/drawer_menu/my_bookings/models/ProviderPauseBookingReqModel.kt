package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models

data class ProviderPauseBookingReqModel(
    val booking_id: Int,
    val key: String,
    val paused_at: String,
    val sp_id: Int
)