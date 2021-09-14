package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models

data class RejectJobPostStatusReqModel(
    val booking_id: Int,
    val key: String,
    val post_job_id: Int,
    val status_id: Int,
    val bid_id: Int
)