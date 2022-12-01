package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models

data class UserPlansTxnResModel(
    val amount: Int,
    val message: String,
    val order_id: String,
    val status: Int,
    val txn_id: String
)