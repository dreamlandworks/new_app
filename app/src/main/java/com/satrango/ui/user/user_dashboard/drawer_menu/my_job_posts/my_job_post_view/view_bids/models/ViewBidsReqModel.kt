package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ViewBidsReqModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("post_job_id")
    val post_job_id: Int
): Serializable