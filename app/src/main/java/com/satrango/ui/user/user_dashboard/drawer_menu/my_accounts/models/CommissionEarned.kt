package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models

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