package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BookingDetail(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("booking_id")
    val booking_id: String,
    @SerializedName("booking_status")
    val booking_status: String,
    @SerializedName("category_id")
    val category_id: String,
    @SerializedName("details")
    val details: List<Detail>,
    @SerializedName("estimate_time")
    val estimate_time: String,
    @SerializedName("estimate_type")
    val estimate_type: String,
    @SerializedName("fname")
    val fname: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("sp_fcm_token")
    val sp_fcm_token: String,
    @SerializedName("user_fcm_token")
    val user_fcm_token: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("pause_status")
    val pause_status: String,
    @SerializedName("action_demand_total_amount")
    val extra_demand_total_amount: String,
    @SerializedName("material_advance")
    val material_advance: String,
    @SerializedName("technician_charges")
    val technician_charges: String,
    @SerializedName("expenditure_incurred")
    val expenditure_incurred: String,
    @SerializedName("otp")
    val otp: String,
    @SerializedName("profile_pic")
    val profile_pic: String,
    @SerializedName("scheduled_date")
    val scheduled_date: String,
    @SerializedName("sp_id")
    val sp_id: String,
    @SerializedName("users_id")
    val users_id: String,
    @SerializedName("started_at")
    val started_at: String,
    @SerializedName("reschedule_id")
    val reschedule_id: String,
    @SerializedName("reschedule_status")
    val reschedule_status: String,
    @SerializedName("req_raised_by")
    val req_raised_by: String,
    @SerializedName("reschedule_date")
    val reschedule_date: String,
    @SerializedName("reschedule_time")
    val reschedule_time: String,
    @SerializedName("reschedule_description")
    val reschedule_description: String,
    @SerializedName("remaining_days_to_start")
    val remaining_days_to_start: Int,
    @SerializedName("remaining_hours_to_start")
    val remaining_hours_to_start: Int,
    @SerializedName("remaining_minutes_to_start")
    val remaining_minutes_to_start: Int,
    @SerializedName("time_slot_id")
    val time_slot_id: String
): Serializable