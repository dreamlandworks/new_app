package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Detail(
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
    @SerializedName("sequence_no")
    val sequence_no: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("weight_type")
    val weight_type: String,
    @SerializedName("zipcode")
    val zipcode: String
): Serializable