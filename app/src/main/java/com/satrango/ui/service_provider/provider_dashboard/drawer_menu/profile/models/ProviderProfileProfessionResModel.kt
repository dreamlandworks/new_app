package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderProfileProfessionResModel(
    @SerializedName("language")
    val language: List<Language>,
    @SerializedName("message")
    val message: String,
    @SerializedName("preferred_time_slots")
    val preferred_time_slots: List<PreferredTimeSlot>,
    @SerializedName("profession")
    val profession: List<Profession>,
    @SerializedName("slot_selection")
    val slot_selection: String,
    @SerializedName("sp_details")
    val sp_details: SpDetails,
    @SerializedName("status")
    val status: Int
): Serializable