package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class PostJobPlansResModel(
    @SerializedName("activated_plan")
    val activated_plan: String,
    @SerializedName("valid_from_date")
    val valid_from_date: String,
    @SerializedName("valid_till_date")
    val valid_till_date: String,
    @SerializedName("wallet_balance")
    val wallet_balance: String,
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable