package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_blue_collar

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.KeywordsResponse
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.LangResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import java.io.Serializable

@Keep
data class PostJobBlueCollarReqModel(
    @SerializedName("attachments")
    val attachments: List<Attachment>,
    @SerializedName("bid_per")
    val bid_per: Int,
    @SerializedName("bid_range_id")
    val bid_range_id: Int,
    @SerializedName("bids_period")
    val bids_period: Int,
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("estimate_time")
    val estimate_time: Int,
    @SerializedName("estimate_type_id")
    val estimate_type_id: Int,
    @SerializedName("job_description")
    val job_description: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("keywords_responses")
    val keywords_responses: List<KeywordsResponse>,
    @SerializedName("lang_responses")
    val lang_responses: List<LangResponse>,
    @SerializedName("scheduled_date")
    val scheduled_date: String,
    @SerializedName("time_slot_from")
    val time_slot_from: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("users_id")
    val users_id: Int
): Serializable