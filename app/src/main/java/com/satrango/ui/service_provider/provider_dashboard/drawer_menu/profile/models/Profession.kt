package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

data class Profession(
    val category_id: String,
    val subcategory_id: String,
    val exp: String,
    val profession_name: String,
    val profession_id: String,
    val skills: List<Skill>,
    val tariff_extra_charges: String,
    val tariff_id: String,
    val tariff_min_charges: String,
    val tariff_per_day: String,
    val tariff_per_hour: String
)