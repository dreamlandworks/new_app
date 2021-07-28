package com.satrango.ui.auth.provider_signup.provider_sign_up_four.models

data class ProviderSignUpFourReqModel(
    val key: String,
    val user_id: String,
    val experience: String,
    val about_me: String,
    val tariff_per_hour: String,
    val tariff_per_day: String,
    val tariff_min_charges: String,
    val tariff_extra_charges: String,
    val id_proof: String,
    val profession_responses: List<ProfessionResponse>,
    val qualification_responses: List<QualificationResponse>,
    val lang_responses: List<LangResponse>,
    val keywords_responses: List<KeywordsResponse>,
    val timeslot_responses: List<TimeslotResponse>
)