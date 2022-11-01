package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.multi_move

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Addresse(
    @SerializedName("address_id")
    val address_id: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("job_description")
    val job_description: String,
    @SerializedName("sequence_no")
    val sequence_no: Int,
    @SerializedName("weight_type")
    val weight_type: Int
): Serializable