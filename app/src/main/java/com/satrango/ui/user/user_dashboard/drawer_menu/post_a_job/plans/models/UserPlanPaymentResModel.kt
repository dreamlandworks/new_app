package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserPlanPaymentResModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("transaction_id")
    val transaction_id: Int
): Serializable