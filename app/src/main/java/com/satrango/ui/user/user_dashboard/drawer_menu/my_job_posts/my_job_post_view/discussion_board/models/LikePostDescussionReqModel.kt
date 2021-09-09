package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models

data class LikePostDescussionReqModel(
    val created_on: String,
    val discussion_tbl_id: Int,
    val key: String,
    val users_id: Int
)