package com.satrango.ui.auth.provider_signup.provider_sign_up_one.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_skills.KeywordResponse
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("experience")
    val experience: List<Experience>,
    @SerializedName("language")
    val language: List<Language>,
    @SerializedName("list_profession")
    val list_profession: List<Profession>,
    @SerializedName("qualification")
    val qualification: List<Qualification>,
    @SerializedName("keywords")
    val keywords: List<KeywordResponse>
): Serializable