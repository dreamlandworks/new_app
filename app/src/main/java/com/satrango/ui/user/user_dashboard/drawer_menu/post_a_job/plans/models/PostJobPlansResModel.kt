package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models

data class PostJobPlansResModel(
    val activated_plan: String,
    val valid_from_date: String,
    val valid_till_date: String,
    val data: List<Data>,
    val message: String,
    val status: Int
)