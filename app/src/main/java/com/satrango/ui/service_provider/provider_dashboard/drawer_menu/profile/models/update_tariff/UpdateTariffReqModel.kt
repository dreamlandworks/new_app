package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_tariff

import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.TimeslotResponse

data class UpdateTariffReqModel(
    val key: String,
    val tariff_extra_charges: String,
    val tariff_min_charges: String,
    val tariff_per_day: String,
    val tariff_per_hour: String,
    val timeslot_responses: List<TimeslotResponse>,
    val user_id: String
)