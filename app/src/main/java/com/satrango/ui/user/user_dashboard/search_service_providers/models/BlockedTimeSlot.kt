package com.satrango.ui.user.user_dashboard.search_service_providers.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BlockedTimeSlot(
    @SerializedName("date")
    val date: String,
    @SerializedName("time_slot_from")
    val time_slot_from: String
): Serializable