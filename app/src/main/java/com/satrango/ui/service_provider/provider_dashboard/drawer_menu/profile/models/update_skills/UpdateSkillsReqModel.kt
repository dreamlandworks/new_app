package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_skills

import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.*

data class UpdateSkillsReqModel(
    val about_me: String,
    val user_id: String,
    val key: String,
    val profession_responses: List<ProfessionResponseX>,
    val qualification_responses: List<QualificationResponse>,
    val lang_responses: List<LangResponse>
)