package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models

data class JobDetail(
    val job_description: String,
    val locality: String,
    val city: String,
    val state: String,
    val zipcode: String,
    val latitude: String,
    val longitude: String,
    val country: String,
    val address_id: String,
    val id: String
)