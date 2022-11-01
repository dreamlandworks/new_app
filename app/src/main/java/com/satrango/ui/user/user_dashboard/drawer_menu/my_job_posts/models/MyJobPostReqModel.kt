package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class MyJobPostReqModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("users_id")
    val users_id: Int
): Serializable