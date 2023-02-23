package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class DiscussionListResModel(
    @SerializedName("discussion_details")
    val discussion_details: List<DiscussionDetail>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable