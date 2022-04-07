package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models

data class UserPlanPaymentReqModel(
    val amount: Int,
    val date: String,
    val key: String,
    val period: Int,
    val plan_id: Int,
    val users_id: Int,
    val w_amount: String,
    val order_id: String,
)