package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_skills

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.*
import java.io.Serializable

@Keep
data class UpdateSkillsReqModel(
    @SerializedName("about_me")
    val about_me: String,
    @SerializedName("user_id")
    val user_id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("profession_responses")
    val profession_responses: List<ProfessionResponseX>,
    @SerializedName("qualification_responses")
    val qualification_responses: List<QualificationResponse>,
    @SerializedName("lang_responses")
    val lang_responses: List<LangResponse>
): Serializable