package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

data class BookingDetails(
    val amount: String,
    val booking_id: Int,
    val completed_at: String,
    val estimate_time: String,
    val estimate_type: String,
    val expenditure_incurred: String,
    val extra_demand_total_amount: String,
    val material_advance: String,
    val scheduled_date: String,
    val scheduled_time: String,
    val started_at: String,
    val technician_charges: String
)