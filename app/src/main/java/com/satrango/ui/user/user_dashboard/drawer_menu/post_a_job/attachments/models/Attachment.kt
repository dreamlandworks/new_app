package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Attachment(
    @SerializedName("file")
    val file: String,
    @SerializedName("type")
    val type: String
): Serializable
