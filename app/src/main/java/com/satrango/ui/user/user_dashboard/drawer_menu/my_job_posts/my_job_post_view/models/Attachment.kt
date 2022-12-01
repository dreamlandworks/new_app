package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Attachment(
    @SerializedName("file_location")
    val file_location: String,
    @SerializedName("file_name")
    val file_name: String,
    @SerializedName("bid_attach_id")
    val bid_attach_id: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("file_type")
    val file_type: String
): Serializable