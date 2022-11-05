package com.satrango.ui.user.user_dashboard.search_service_providers.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class SlotsData(
    @SerializedName("blocked_tile_slots")
    val blocked_time_slots: List<BlockedTimeSlot>,
    @SerializedName("preferred_time_slots")
    val preferred_time_slots: List<PreferredTimeSlot>,
    @SerializedName("user_id")
    val user_id: String
): Serializable