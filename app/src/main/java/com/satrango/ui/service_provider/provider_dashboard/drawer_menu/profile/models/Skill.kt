package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Skill(
    @SerializedName("keyword")
    val keyword: String,
    @SerializedName("keywords_id")
    val keywords_id: Int
): Serializable