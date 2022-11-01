package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class RejectJobPostStatusReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("post_job_id")
    val post_job_id: Int,
    @SerializedName("status_id")
    val status_id: Int,
    @SerializedName("bid_id")
    val bid_id: Int
): Serializable