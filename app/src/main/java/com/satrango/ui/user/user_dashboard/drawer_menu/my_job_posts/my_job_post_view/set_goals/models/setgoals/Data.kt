package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.setgoals

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("created_dts")
    val created_dts: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("goal_id")
    val goal_id: String,
    @SerializedName("status")
    val status: String
): Serializable