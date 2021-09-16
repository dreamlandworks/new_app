package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models

data class ExtraDemandReqModel(
    val booking_id: String,
    val created_on: String,
    val amount: String,
    val material_advance: String,
    val technician_charges: String,
    val key: String
)