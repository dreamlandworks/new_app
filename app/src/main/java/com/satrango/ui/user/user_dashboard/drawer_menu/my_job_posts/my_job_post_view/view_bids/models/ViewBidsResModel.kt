package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models

data class ViewBidsResModel(
    val bid_details: List<BidDetail>,
    val message: String,
    val status: Int
)