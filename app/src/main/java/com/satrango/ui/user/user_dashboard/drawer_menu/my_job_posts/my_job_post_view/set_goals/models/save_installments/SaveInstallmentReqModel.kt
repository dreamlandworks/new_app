package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class SaveInstallmentReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("data")
    val data: List<DataX>,
    @SerializedName("key")
    val key: String,
    @SerializedName("post_job_id")
    val post_job_id: Int
): Serializable