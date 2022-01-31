package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

data class ProviderInvoiceResModel(
    val booking_details: BookingDetails,
    val booking_paid_transactions: List<BookingPaidTransaction>,
    val message: String,
    val status: Int
)