package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class MyAccountDetailsResModel(
    @SerializedName("commission_earned")
    val commission_earned: CommissionEarned,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("total_bookings")
    val total_bookings: String,
    @SerializedName("total_job_posts")
    val total_job_posts: String,
    @SerializedName("wallet_balance")
    val wallet_balance: Double,
    @SerializedName("wallet_blocked_amount")
    val wallet_blocked_amount: Double,
    @SerializedName("total_referrals")
    val total_referrals: String,
    @SerializedName("activated_plan")
    val activated_plan: String
): Serializable