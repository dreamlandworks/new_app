package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

data class ProviderInvoiceReqModel(
    val booking_id: Int,
    val key: String,
    val extra_demand: String // 0 - Not Raised, 1 - Raised
)