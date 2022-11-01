package com.satrango.ui.user.bookings.view_booking_details.installments_request.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class PostApproveRejectReqModel(
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("inst_id")
    val inst_id: Int,
    @SerializedName("key")
    val key: String,
    @SerializedName("sp_id")
    val sp_id: Int,
    @SerializedName("status_id")
    val status_id: Int,
    @SerializedName("users_id")
    val users_id: Int
): Serializable