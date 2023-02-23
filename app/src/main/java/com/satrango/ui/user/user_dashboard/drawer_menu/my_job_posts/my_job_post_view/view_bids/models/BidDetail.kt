package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BidDetail(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("bid_id")
    val bid_id: String,
    @SerializedName("estimate_time")
    val esimate_time: String,
    @SerializedName("estimate_type")
    val estimate_type: String,
    @SerializedName("proposal")
    val proposal: String,
    @SerializedName("sp_profile")
    val sp_profile: String,
    @SerializedName("bid_type")
    val bid_type: String,
    @SerializedName("sp_fcm_token")
    val sp_fcm_token: String,
    @SerializedName("sp_fname")
    val sp_fname: String,
    @SerializedName("sp_id")
    val sp_id: String,
    @SerializedName("users_id")
    val users_id: String,
    @SerializedName("job_title")
    val job_title: String,
    @SerializedName("sp_lname")
    val sp_lname: String,
    @SerializedName("sp_mobile")
    val sp_mobile: String,
    @SerializedName("about_me")
    val about_me: String,
    @SerializedName("jobs_completed")
    val jobs_completed: Int
): Serializable