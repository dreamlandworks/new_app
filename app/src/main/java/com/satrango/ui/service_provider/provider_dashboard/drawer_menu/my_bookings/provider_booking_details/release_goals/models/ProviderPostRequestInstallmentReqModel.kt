package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models

data class ProviderPostRequestInstallmentReqModel(
    val booking_id: Int,
    val inst_id: Int,
    val key: String,
    val sp_id: Int,
    val users_id: Int
)