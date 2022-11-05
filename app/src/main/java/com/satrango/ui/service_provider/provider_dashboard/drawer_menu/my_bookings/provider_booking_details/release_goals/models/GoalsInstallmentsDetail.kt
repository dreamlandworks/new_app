package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class GoalsInstallmentsDetail(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("created_dts")
    val created_dts: String,
    @SerializedName("goal_id")
    val goal_id: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("inst_no")
    val inst_no: String,
    @SerializedName("status_id")
    val status_id: String,
    @SerializedName("inst_paid_status")
    val inst_paid_status: String,
    @SerializedName("inst_status_id")
    val inst_status_id: String,
    @SerializedName("post_job_id")
    val post_job_id: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("transaction_id")
    val transaction_id: String
): Serializable