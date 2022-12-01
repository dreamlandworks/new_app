package com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Address(
    @SerializedName("id")
    val id: String,
    @SerializedName("apartment_name")
    val apartment_name: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("flat_no")
    val flat_no: String,
    @SerializedName("landmark")
    val landmark: String,
    @SerializedName("locality")
    val locality: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("zipcode")
    val zipcode: String
): Serializable