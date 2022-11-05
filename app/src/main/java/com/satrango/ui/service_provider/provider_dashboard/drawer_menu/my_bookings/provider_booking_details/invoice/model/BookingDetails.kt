package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class BookingDetails(
    @SerializedName("sp_id")
    val sp_id: String,
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("scheduled_date")
    val scheduled_date: String,
    @SerializedName("scheduled_time")
    val scheduled_time: String,
    @SerializedName("started_at")
    val started_at: String,
//    val txn_id: String,
//    val order_id: String,
    @SerializedName("completed_at")
    val completed_at: String,
    @SerializedName("estimate_time")
    val estimate_time: String,
    @SerializedName("estimate_type")
    val estimate_type: String,
    @SerializedName("time_lapsed")
    val time_lapsed: String,
    @SerializedName("paid")
    val paid: String,
    @SerializedName("cgst_tax")
    val cgst_tax: String,
    @SerializedName("wallet_balance")
    val wallet_balance: String,
    @SerializedName("finish_OTP")
    val finish_OTP: String,
    @SerializedName("sgst_tax")
    val sgst_tax: String,
    @SerializedName("extra_demand_total_amount")
    val extra_demand_total_amount: String,
    @SerializedName("material_advance")
    val material_advance: String,
    @SerializedName("technician_charges")
    val technician_charges: String,
    @SerializedName("expenditure_incurred")
    val expenditure_incurred: String,
    @SerializedName("final_dues")
    val final_dues: String,
    @SerializedName("dues")
    val dues: String
): Serializable