package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models

data class ProviderGoalsInstallmentsListResModel(
    val goals_installments_details: List<GoalsInstallmentsDetail>,
    val message: String,
    val status: Int
)