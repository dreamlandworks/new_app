package com.satrango.ui.auth.provider_signup.provider_sign_up_four.models

data class ProfessionResponseX(
    var keywords_responses: ArrayList<KeywordsResponse>,
    var tariff_extra_charges: String,
    var tariff_min_charges: String,
    var tariff_per_day: String,
    var tariff_per_hour: String,
    var experience: String,
    val name: String,
    val prof_id: String
)