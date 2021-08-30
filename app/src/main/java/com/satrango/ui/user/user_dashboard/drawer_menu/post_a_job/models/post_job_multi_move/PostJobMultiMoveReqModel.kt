package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_multi_move

import com.satrango.ui.user.bookings.booking_address.models.Attachment
import com.satrango.ui.user.bookings.booking_attachments.models.Addresses
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.KeywordsResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.LangResponse

data class PostJobMultiMoveReqModel(
    val addresses: List<Addresses>,
    val attachments: List<Attachment>,
    val bid_per: Int,
    val bid_range_id: Int,
    val bids_period: Int,
    val created_on: String,
    val estimate_time: Int,
    val estimate_type_id: Int,
    val key: String,
    val keywords_responses: List<KeywordsResponse>,
    val lang_responses: List<LangResponse>,
    val scheduled_date: String,
    val time_slot_from: String,
    val title: String,
    val users_id: Int
)