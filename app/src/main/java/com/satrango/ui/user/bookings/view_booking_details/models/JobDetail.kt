package com.satrango.ui.user.bookings.view_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class JobDetail(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("job_description")
    val job_description: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("locality")
    val locality: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("zipcode")
    val zipcode: String
): Serializable