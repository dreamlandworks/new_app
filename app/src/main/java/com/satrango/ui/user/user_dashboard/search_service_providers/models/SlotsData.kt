package com.satrango.ui.user.user_dashboard.search_service_providers.models

data class SlotsData(
    val blocked_time_slots: List<BlockedTimeSlot>,
    val preferred_time_slots: List<PreferredTimeSlot>,
    val user_id: String
)