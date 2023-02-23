package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class JobPostDetail(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("average_bids_amount")
    val average_bids_amount: String,
    @SerializedName("bid_end_date")
    val bid_end_date: String,
    @SerializedName("bid_range_name")
    val bid_range_name: String,
    @SerializedName("bids_period")
    val bids_period: String,
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("awarded_to")
    val awarded_to: String,
    @SerializedName("awarded_to_sp_profile_pic")
    val awarded_to_sp_profile_pic: String,
    @SerializedName("booking_status")
    val booking_status: String,
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("current_date")
    val current_date: String,
    @SerializedName("details")
    val details: List<Detail>,
    @SerializedName("estimate_time")
    val estimate_time: String,
    @SerializedName("estimate_type")
    val estimate_type: String,
    @SerializedName("expires_in")
    val expires_in: String,
    @SerializedName("expired_on")
    val expired_on: String,
    @SerializedName("fname")
    val fname: Any,
    @SerializedName("from")
    val from: String,
    @SerializedName("lname")
    val lname: Any,
    @SerializedName("mobile")
    val mobile: Any,
    @SerializedName("post_created_on")
    val post_created_on: String,
    @SerializedName("post_job_id")
    val post_job_id: String,
    @SerializedName("post_job_ref_id")
    val post_job_ref_id: String,
    @SerializedName("profile_pic")
    val profile_pic: Any,
    @SerializedName("range_slots")
    val range_slots: String,
    @SerializedName("scheduled_date")
    val scheduled_date: String,
    @SerializedName("sp_id")
    val sp_id: String,
    @SerializedName("started_at")
    val started_at: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("total_bids")
    val total_bids: Int
): Serializable