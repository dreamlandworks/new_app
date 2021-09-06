package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models

data class DiscussionListResModel(
    val discussion_details: List<DiscussionDetail>,
    val message: String,
    val status: Int
)