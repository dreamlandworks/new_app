package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class JobDetail(
    @SerializedName("job_description")
    val job_description: String,
    @SerializedName("locality")
    val locality: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("zipcode")
    val zipcode: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("address_id")
    val address_id: String,
    @SerializedName("id")
    val id: String
): Serializable