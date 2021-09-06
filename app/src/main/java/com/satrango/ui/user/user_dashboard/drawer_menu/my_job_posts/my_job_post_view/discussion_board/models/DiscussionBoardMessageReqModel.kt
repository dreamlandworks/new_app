package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models

import com.satrango.ui.user.bookings.booking_address.models.Attachment

data class DiscussionBoardMessageReqModel(
    val attachment_type: String,
    val attachments: List<Attachment>,
    val comment: String,
    val created_on: String,
    val key: String,
    val post_job_id: Int,
    val users_id: Int
)