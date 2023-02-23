package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class MyJobPostViewResModel(
    @SerializedName("attachments")
    val attachments: List<Attachment>,
    @SerializedName("job_details")
    val job_details: List<JobDetail>,
    @SerializedName("job_post_details")
    val job_post_details: JobPostDetails,
    @SerializedName("keywords")
    val keywords: List<String>,
    @SerializedName("languages")
    val languages: List<String>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable