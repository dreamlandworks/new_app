package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserPlanPaymentReqModel(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("period")
    val period: Int,
    @SerializedName("plan_id")
    val plan_id: Int,
    @SerializedName("users_id")
    val users_id: Int,
    @SerializedName("w_amount")
    val w_amount: String,
    @SerializedName("order_id")
    val order_id: String,
): Serializable