package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.installment_payments

data class InstallmentPaymentReqModel(
    val amount: String,
    val booking_id: Int,
    val date: String,
    val installment_det_id: Int,
    val key: String,
    val payment_status: String,
    val reference_id: String,
    val users_id: Int
)