package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models

data class ProviderMyAccountResModel(
    val commission_earned: CommissionEarned,
    val message: String,
    val status: Int,
    val total_bids: Int,
    val total_bookings: Int,
    val total_reviews: String,
    val total_completed_bids: Int,
    val total_completed_bookings: Int,
    val total_referrals: String
)