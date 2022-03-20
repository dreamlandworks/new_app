package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

data class BookingDetails(
    val sp_id: String,
    val booking_id: Int,
    val scheduled_date: String,
    val scheduled_time: String,
    val started_at: String,
    val completed_at: String,
    val estimate_time: String,
    val estimate_type: String,
    val time_lapsed: String,
    val paid: String,
    val cgst_tax: String,
    val wallet_balance: String,
    val finish_OTP: String,
    val sgst_tax: String,
    val extra_demand_total_amount: String,
    val material_advance: String,
    val technician_charges: String,
    val expenditure_incurred: String,
    val final_dues: String,
    val dues: String
)