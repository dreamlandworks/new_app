package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderMyAccountResModel(
    @SerializedName("commission_earned")
    val commission_earned: CommissionEarned,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("total_bids")
    val total_bids: Int,
    @SerializedName("total_bookings")
    val total_bookings: Int,
    @SerializedName("total_reviews")
    val total_reviews: String,
    @SerializedName("total_completed_bids")
    val total_completed_bids: Int,
    @SerializedName("total_completed_bookings")
    val total_completed_bookings: Int,
    @SerializedName("wallet_balance")
    val wallet_balance: Double,
    @SerializedName("total_referrals")
    val total_referrals: String,
    @SerializedName("activated_plan")
    val activated_plan: String,
): Serializable