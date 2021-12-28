package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move

import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.KeywordsResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.LangResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment

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
    val users_id: Int,
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val postal_code: String,
    val user_lat: String,
    val user_long: String,
)