package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_tariff

import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.TimeslotResponse

data class UpdateTariffReqModel(
    val key: String,
    val timeslot_responses: List<TimeslotResponse>,
    val user_id: String
)