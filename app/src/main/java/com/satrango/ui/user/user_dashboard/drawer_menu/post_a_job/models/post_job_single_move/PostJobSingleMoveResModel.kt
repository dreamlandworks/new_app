package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class PostJobSingleMoveResModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("post_job_id")
    val post_job_id: Int,
    @SerializedName("post_job_ref_id")
    val post_job_ref_id: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("user_plan_id")
    val user_plan_id: String,
): Serializable