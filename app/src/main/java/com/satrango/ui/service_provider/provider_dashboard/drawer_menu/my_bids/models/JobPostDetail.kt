package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models

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
    @SerializedName("bid_per")
    val bid_per: String,
    @SerializedName("bid_range_name")
    val bid_range_name: String,
    @SerializedName("bid_type")
    val bid_type: String,
    @SerializedName("bids_period")
    val bids_period: String,
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("booking_status")
    val booking_status: String,
    @SerializedName("current_date")
    val current_date: String,
    @SerializedName("estimate_time")
    val estimate_time: String,
    @SerializedName("estimate_type")
    val estimate_type: String,
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("ecpires_in")
    val expires_in: String,
    @SerializedName("fcm_token")
    val fcm_token: String,
    @SerializedName("booking_user_id")
    val booking_user_id: String,
    @SerializedName("fname")
    val fname: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("job_post_description")
    val job_post_description: List<JobPostDescription>,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("post_created_on")
    val post_created_on: String,
    @SerializedName("bid_id")
    val bid_id: String,
    @SerializedName("post_job_id")
    val post_job_id: String,
    @SerializedName("post_job_ref_id")
    val post_job_ref_id: String,
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
    @SerializedName("distance_miles")
    val distance_miles: String,
    @SerializedName("total_bids")
    val total_bids: Int
): Serializable