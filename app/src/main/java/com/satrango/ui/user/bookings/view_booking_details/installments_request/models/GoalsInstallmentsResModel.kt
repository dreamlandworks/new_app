package com.satrango.ui.user.bookings.view_booking_details.installments_request.models

data class GoalsInstallmentsResModel(
    val goals_installments_details: List<GoalsInstallmentsDetail>,
    val message: String,
    val status: Int
)