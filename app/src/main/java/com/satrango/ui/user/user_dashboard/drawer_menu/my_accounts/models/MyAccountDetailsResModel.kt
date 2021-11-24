package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models

data class MyAccountDetailsResModel(
    val commission_earned: CommissionEarned,
    val message: String,
    val status: Int,
    val total_bookings: String,
    val total_job_posts: String,
    val wallet_balance: Double,
    val wallet_blocked_amount: Double,
    val total_referrals: String,
    val activated_plan: String
)