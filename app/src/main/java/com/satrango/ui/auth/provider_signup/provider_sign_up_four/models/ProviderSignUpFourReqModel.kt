package com.satrango.ui.auth.provider_signup.provider_sign_up_four.models

data class ProviderSignUpFourReqModel(
    val about_me: String,
    val id_proof: String,
    val key: String,
    val lang_responses: List<LangResponse>,
    val profession_responses: List<ProfessionResponseX>,
    val qualification_responses: List<QualificationResponse>,
    val timeslot_responses: List<TimeslotResponse>,
    val user_id: String
)