package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class DiscussionDetail(
    @SerializedName("attachment_count")
    val attachment_count: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("file_name")
    val file_name: String,
    @SerializedName("fname")
    val fname: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("likes_count")
    val likes_count: String,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("post_job_id")
    val post_job_id: String,
    @SerializedName("users_id")
    val users_id: String,
    @SerializedName("profile_pic")
    val profile_pic: String,
    @SerializedName("created_dts")
    val created_dts: String,
): Serializable