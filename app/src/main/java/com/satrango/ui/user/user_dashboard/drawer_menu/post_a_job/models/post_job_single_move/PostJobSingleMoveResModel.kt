package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move

data class PostJobSingleMoveResModel(
    val message: String,
    val post_job_id: Int,
    val post_job_ref_id: String,
    val status: Int,
    val user_plan_id: String,

)