package com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class DataX(
    @SerializedName("apartment_name")
    val apartment_name: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("flat_no")
    val flat_no: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("landmark")
    val landmark: String,
    @SerializedName("locality")
    val locality: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("zipcode")
    val zipcode: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String
): Serializable