package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("customer_support")
    val customer_support: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("period")
    val period: String,
    @SerializedName("posts_per_month")
    val posts_per_month: String,
    @SerializedName("premium_tag")
    val premium_tag: String,
    @SerializedName("proposals_per_post")
    val proposals_per_post: String
): Serializable