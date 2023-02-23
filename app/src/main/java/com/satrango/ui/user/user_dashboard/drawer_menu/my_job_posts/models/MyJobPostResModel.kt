package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class MyJobPostResModel(
    @SerializedName("job_post_details")
    val job_post_details: List<JobPostDetail>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable