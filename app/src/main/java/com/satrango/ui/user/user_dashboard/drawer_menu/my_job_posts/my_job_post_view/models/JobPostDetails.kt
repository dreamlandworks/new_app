package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models

data class JobPostDetails(
    val amount: String,
    val bid_range_name: String,
    val bids_period: String,
    val booking_status: String,
    val estimate_time: String,
    val estimate_type: String,
    val fcm_token: String,
    val fname: String,
    val from: String,
    val lname: String,
    val mobile: String,
    val post_created_on: String,
    val post_job_id: String,
    val post_job_ref_id: String,
    val range_slots: String,
    val scheduled_date: String,
    val sp_id: String,
    val bid_end_date: String,
    val expires_in: String,
    val total_bids: Int,
    val average_bids_amount: String,
    val started_at: String,
    val title: String
)