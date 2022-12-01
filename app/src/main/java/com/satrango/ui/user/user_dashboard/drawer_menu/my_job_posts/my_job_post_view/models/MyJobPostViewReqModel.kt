package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class MyJobPostViewReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("category_id")
    val category_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("post_job_id")
    val post_job_id: Int,
    @SerializedName("users_id")
    val users_id: Int
): Serializable