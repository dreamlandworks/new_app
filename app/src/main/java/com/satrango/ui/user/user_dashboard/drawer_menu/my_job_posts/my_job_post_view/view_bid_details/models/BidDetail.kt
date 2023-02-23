package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BidDetail(
    @SerializedName("about_me")
    val about_me: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("attachment_count")
    val attachment_count: String,
    @SerializedName("bid_id")
    val bid_id: String,
    @SerializedName("estimate_time")
    val estimate_time: String,
    @SerializedName("estimate_type")
    val estimate_type: String,
    @SerializedName("exp")
    val exp: String,
    @SerializedName("place")
    val place: String,
    @SerializedName("languages")
    val languages: String,
    @SerializedName("jobs_completed")
    val jobs_completed: String,
    @SerializedName("profession")
    val profession: String,
    @SerializedName("proposal")
    val proposal: String,
    @SerializedName("qualification")
    val qualification: String,
    @SerializedName("sp_fcm_token")
    val sp_fcm_token: Any,
    @SerializedName("sp_fname")
    val sp_fname: String,
    @SerializedName("job_title")
    val job_title: String,
    @SerializedName("sp_gender")
    val sp_gender: String,
    @SerializedName("sp_id")
    val sp_id: String,
    @SerializedName("sp_lname")
    val sp_lname: String,
    @SerializedName("sp_mobile")
    val sp_mobile: String,
    @SerializedName("sp_profile")
    val sp_profile: String
): Serializable