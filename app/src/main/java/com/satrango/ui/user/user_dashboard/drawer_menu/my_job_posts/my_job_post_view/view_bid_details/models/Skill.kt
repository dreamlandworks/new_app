package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Skill(
    @SerializedName("keyword")
    val keyword: String,
    @SerializedName("keywords_id")
    val keywords_id: String
): Serializable