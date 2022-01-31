package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments

data class SaveInstallmentReqModel(
    val booking_id: Int,
    val data: List<DataX>,
    val key: String,
    val post_job_id: Int
)