package com.satrango.ui.user.user_dashboard.user_alerts.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Action(
    @SerializedName("accept_response")
    val accept_response: String,
    @SerializedName("accept_text")
    val accept_text: String,
    @SerializedName("api")
    val api: String,
    @SerializedName("bid_id")
    val bid_id: String,
    @SerializedName("bid_user_id")
    val bid_user_id: String,
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("post_id")
    val post_id: String,
    @SerializedName("profile_pic")
    val profile_pic: String,
    @SerializedName("reject_response")
    val reject_response: String,
    @SerializedName("reject_text")
    val reject_text: String,
    @SerializedName("req_raised_by_id")
    val req_raised_by_id: String,
    @SerializedName("reschedule_id")
    val reschedule_id: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_code_id")
    val status_code_id: String,
    @SerializedName("type_id")
    val type_id: String,
    @SerializedName("updated_on")
    val updated_on: String,
    @SerializedName("user_id")
    val user_id: String,
    @SerializedName("sp_id")
    val sp_id: String,
): Serializable