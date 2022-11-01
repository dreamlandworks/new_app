package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class DataX(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("goal_id")
    val goal_id: Int,
    @SerializedName("inst_no")
    val inst_no: Int
): Serializable