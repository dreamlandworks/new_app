package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class PreferredTimeSlot(
    @SerializedName("time_slot_id")
    val time_slot_id: String,
    @SerializedName("day_slots")
    val day_slots: String,
    @SerializedName("time_slot_from")
    val time_slot_from: String,
    @SerializedName("time_slot_to")
    val time_slot_to: String
): Serializable