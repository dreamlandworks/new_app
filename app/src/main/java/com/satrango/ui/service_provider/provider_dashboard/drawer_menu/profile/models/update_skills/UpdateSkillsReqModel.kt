package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_skills

import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.KeywordsResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.LangResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProfessionResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.QualificationResponse

data class UpdateSkillsReqModel(
    val about_me: String,
    val experience: String,
    val key: String,
    val keywords_responses: List<KeywordsResponse>,
    val lang_responses: List<LangResponse>,
    val profession_responses: List<ProfessionResponse>,
    val qualification_responses: List<QualificationResponse>,
    val user_id: String
)