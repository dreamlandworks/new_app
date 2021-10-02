package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

data class ProviderProfileProfessionResModel(
    val language: List<Language>,
    val message: String,
    val preferred_time_slots: List<PreferredTimeSlot>,
    val skills: List<Skill>,
    val sp_details: SpDetail,
    val status: Int
)