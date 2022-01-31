package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

data class ProviderProfileProfessionResModel(
    val language: List<Language>,
    val message: String,
    val preferred_time_slots: List<PreferredTimeSlot>,
    val profession: List<Profession>,
    val slot_selection: String,
    val sp_details: SpDetails,
    val status: Int
)