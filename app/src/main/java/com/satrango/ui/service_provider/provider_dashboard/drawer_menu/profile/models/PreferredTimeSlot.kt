package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

data class PreferredTimeSlot(
    val time_slot_id: String,
    val day_slots: String,
    val time_slot_from: String,
    val time_slot_to: String
)