package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.single_move

import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.KeywordsResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.LangResponse

data class MyJobPostSingleMoveEditReqModel(
    val address_id: Int,
    val attachments: List<Attachment>,
    val bid_per: Int,
    val bid_range_id: Int,
    val bids_period: Int,
    val booking_id: Int,
    val created_on: String,
    val estimate_time: Int,
    val estimate_type_id: Int,
    val job_description: String,
    val key: String,
    val keywords_responses: List<KeywordsResponse>,
    val lang_responses: List<LangResponse>,
    val post_job_id: Int,
    val scheduled_date: String,
    val time_slot_from: String,
    val title: String,
    val users_id: Int
)