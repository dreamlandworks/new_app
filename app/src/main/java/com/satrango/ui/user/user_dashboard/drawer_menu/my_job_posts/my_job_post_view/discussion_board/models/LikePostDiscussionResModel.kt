package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class LikePostDiscussionResModel(
    @SerializedName("arr_like_id")
    val arr_like_id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable