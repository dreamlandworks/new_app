package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models

data class ViewProposalResModel(
    val attachments: List<Attachment>,
    val bid_details: BidDetail,
    val language: List<Language>,
    val message: String,
    val skills: List<Skill>,
    val status: Int
)