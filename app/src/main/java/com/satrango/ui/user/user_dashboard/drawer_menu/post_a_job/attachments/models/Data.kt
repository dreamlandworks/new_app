package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("keyword")
    val keyword: String,
    @SerializedName("keyword_id")
    val keyword_id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("profession_id")
    val profession_id: String,
    @SerializedName("subcategory_id")
    val subcategory_id: String
): Serializable