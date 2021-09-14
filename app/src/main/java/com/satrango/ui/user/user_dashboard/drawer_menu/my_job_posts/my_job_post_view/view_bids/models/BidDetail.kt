package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models

data class BidDetail(
    val amount: String,
    val bid_id: String,
    val esimate_time: String,
    val estimate_type: String,
    val proposal: String,
    val sp_profile: String,
    val bid_type: String,
    val sp_fcm_token: String,
    val sp_fname: String,
    val sp_id: String,
    val sp_lname: String,
    val sp_mobile: String,
    val about_me: String,
    val jobs_completed: Int
)