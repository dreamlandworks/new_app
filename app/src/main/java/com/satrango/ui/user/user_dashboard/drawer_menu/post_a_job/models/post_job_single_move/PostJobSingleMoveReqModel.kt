package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move

import com.satrango.ui.user.bookings.booking_address.models.Attachment

data class PostJobSingleMoveReqModel(
    val address_id: Int,
    val attachments: List<Attachment>,
    val bid_per: Int,
    val bid_range_id: Int,
    val bids_period: Int,
    val created_on: String,
    val estimate_time: Int,
    val estimate_type_id: Int,
    val job_description: String,
    val key: String,
    val keywords_responses: List<KeywordsResponse>,
    val lang_responses: List<LangResponse>,
    val scheduled_date: String,
    val time_slot_from: String,
    val title: String,
    val users_id: Int
)