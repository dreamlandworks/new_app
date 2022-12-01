package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Language(
    @SerializedName("language_id")
    val language_id: String,
    @SerializedName("name")
    val name: String
): Serializable