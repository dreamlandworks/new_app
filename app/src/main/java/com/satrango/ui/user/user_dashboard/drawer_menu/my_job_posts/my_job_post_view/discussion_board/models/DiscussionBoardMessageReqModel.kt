package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import java.io.Serializable

@Keep
data class DiscussionBoardMessageReqModel(
    @SerializedName("attachment_type")
    val attachment_type: String,
    @SerializedName("attachments")
    val attachments: List<Attachment>,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("post_job_id")
    val post_job_id: Int,
    @SerializedName("users_id")
    val users_id: Int,
    @SerializedName("user_type")
    val user_type: String
): Serializable