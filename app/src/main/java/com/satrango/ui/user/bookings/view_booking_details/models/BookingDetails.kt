package com.satrango.ui.user.bookings.view_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BookingDetails(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("final_amount")
    val final_amount: String,
    @SerializedName("estimate_time")
    val estimate_time: String,
    @SerializedName("estimate_type")
    val estimate_type: String,
    @SerializedName("fcm_token")
    val fcm_token: String,
    @SerializedName("sp_fcm_token")
    val sp_fcm_token: String,
    @SerializedName("fname")
    val fname: String,
    @SerializedName("device_id")
    val device_id: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("sp_profile_pic")
    val sp_profile_pic: String,
    @SerializedName("booking_status")
    val booking_status: String,
    @SerializedName("user_profile_pic")
    val user_profile_pic: String,
    @SerializedName("time_slot_id")
    val time_slot_id: String,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("sp_lname")
    val sp_lname: String,
    @SerializedName("sp_fname")
    val sp_fname: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("post_job_id")
    val post_job_id: String,
    @SerializedName("extra_demand_status")
    val extra_demand_status: String,
    @SerializedName("extra_demand_total_amount")
    val extra_demand_total_amount: String,
    @SerializedName("material_advance")
    val material_advance: String,
    @SerializedName("technician_charges")
    val technician_charges: String,
    @SerializedName("expenditure_incurred")
    val expenditure_incurred: String,
    @SerializedName("scheduled_date")
    val scheduled_date: String,
    @SerializedName("time_lapsed")
    val time_lapsed: String,
    @SerializedName("started_at")
    val started_at: String,
    @SerializedName("sp_id")
    val sp_id: String,
    @SerializedName("reschedule_status")
    val reschedule_status: String,
    @SerializedName("otp")
    val otp: String,
    @SerializedName("otp_raised_by")
    val otp_raised_by: String,
    @SerializedName("sp_profession")
    val sp_profession: String,
    @SerializedName("remaining_days_to_start")
    val remaining_days_to_start: String,
    @SerializedName("remaining_hours_to_start")
    val remaining_hours_to_start: String,
    @SerializedName("remaining_minutes_to_start")
    val remaining_minutes_to_start: String,
): Serializable