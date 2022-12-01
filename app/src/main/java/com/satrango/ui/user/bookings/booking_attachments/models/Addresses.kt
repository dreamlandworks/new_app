package com.satrango.ui.user.bookings.booking_attachments.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Addresses(
    @SerializedName("address_id")
    val address_id: Int,
    @SerializedName("job_description")
    val job_description: String,
    @SerializedName("sequence_no")
    val sequence_no: Int,
    @SerializedName("weight_type")
    val weight_type: Int,
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("postal_code")
    val postal_code: String,
    @SerializedName("user_lat")
    val user_lat: String,
    @SerializedName("user_long")
    val user_long: String,
): Serializable