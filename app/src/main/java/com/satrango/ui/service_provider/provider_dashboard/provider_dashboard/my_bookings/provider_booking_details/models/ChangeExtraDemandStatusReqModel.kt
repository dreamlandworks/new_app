package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models

data class ChangeExtraDemandStatusReqModel(
    val booking_id: Int,
    val key: String,
    val status_id: Int
)