package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ViewProposalReqModel(
    @SerializedName("bid_id")
    val bid_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("sp_id")
    val sp_id: Int
): Serializable