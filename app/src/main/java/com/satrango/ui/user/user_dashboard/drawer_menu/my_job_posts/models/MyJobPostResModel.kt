package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models

data class MyJobPostResModel(
    val job_post_details: List<JobPostDetail>,
    val message: String,
    val status: Int
)