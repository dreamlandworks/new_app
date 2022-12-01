package com.satrango.ui.user.bookings.booking_address.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment
import java.io.Serializable

@Keep
data class BlueCollarBookingReqModel(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("attachments")
    val attachments: ArrayList<Attachment>,
    @SerializedName("created_on")
    val created_on: String,
    @SerializedName("estimate_time")
    val estimate_time: Int,
    @SerializedName("estimate_type_id")
    val estimate_type_id: Int,
    @SerializedName("job_description")
    val job_description: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("scheduled_date")
    val scheduled_date: String,
    @SerializedName("sp_id")
    val sp_id: Int,
    @SerializedName("started_at")
    val started_at: String,
    @SerializedName("time_slot_from")
    val time_slot_from: String,
    @SerializedName("time_slot_to")
    val time_slot_to: String,
    @SerializedName("users_id")
    val users_id: Int,
    @SerializedName("cgst")
    val cgst: String,
    @SerializedName("sgst")
    val sgst: String,
    @SerializedName("profession_id")
    val profession_id: String
): Serializable