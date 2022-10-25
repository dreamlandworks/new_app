package com.satrango.ui.auth.provider_signup.provider_sign_up_one.models

import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_skills.KeywordResponse

data class Data(
    val experience: List<Experience>,
    val language: List<Language>,
    val list_profession: List<Profession>,
    val qualification: List<Qualification>,
    val keywords: List<KeywordResponse>
)