package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models

data class UserPlanPaymentReqModel(
    val amount: Int,
    val date: String,
    val key: String,
    val payment_status: String,
    val period: Int,
    val plan_id: Int,
    val reference_id: String,
    val users_id: Int
)