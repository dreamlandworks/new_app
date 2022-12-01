package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class JobPostDescription(
    @SerializedName("address_id")
    val address_id: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("id")
    val id: String,
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