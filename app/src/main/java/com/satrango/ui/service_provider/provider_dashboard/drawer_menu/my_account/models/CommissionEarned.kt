package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class CommissionEarned(
    @SerializedName("change")
    val change: Int,
    @SerializedName("prev_month")
    val prev_month: Int,
    @SerializedName("this_month")
    val this_month: Int
): Serializable