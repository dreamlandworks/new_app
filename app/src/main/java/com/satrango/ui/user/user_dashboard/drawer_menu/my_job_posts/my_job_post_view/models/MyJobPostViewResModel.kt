package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models

data class MyJobPostViewResModel(
    val attachments: List<Attachment>,
    val job_details: List<JobDetail>,
    val job_post_details: JobPostDetails,
    val keywords: List<String>,
    val languages: List<String>,
    val message: String,
    val status: Int
)