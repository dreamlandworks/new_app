package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Language(
    @SerializedName("language_id")
    val language_id: String,
    @SerializedName("name")
    val name: String
): Serializable