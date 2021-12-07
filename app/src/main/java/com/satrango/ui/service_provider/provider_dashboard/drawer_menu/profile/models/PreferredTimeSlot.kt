package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

data class PreferredTimeSlot(
    val day_slot: String,
    val id: String,
    val time_slot_from: String,
    val time_slot_id: String,
    val time_slot_to: String,
    val users_id: String
)