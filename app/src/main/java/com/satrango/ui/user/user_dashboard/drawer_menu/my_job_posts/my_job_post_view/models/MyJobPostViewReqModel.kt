package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models

data class MyJobPostViewReqModel(
    val booking_id: Int,
    val category_id: Int,
    val key: String,
    val post_job_id: Int,
    val users_id: Int
)