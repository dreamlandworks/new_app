package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment
import java.io.Serializable

@Keep
data class ViewProposalResModel(
    @SerializedName("attachments")
    val attachments: List<Attachment>,
    @SerializedName("bid_details")
    val bid_details: BidDetail,
    @SerializedName("language")
    val language: List<Language>,
    @SerializedName("message")
    val message: String,
    @SerializedName("skills")
    val skills: List<Skill>,
    @SerializedName("status")
    val status: Int
): Serializable