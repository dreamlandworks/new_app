package com.satrango.ui.user.bookings.change_address

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class AddBookingAddressReqModel(
    @SerializedName("address")
    val address: String,
    @SerializedName("apartment")
    val apartment: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("flat")
    val flat: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("landmark")
    val landmark: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("postal_code")
    val postal_code: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("user_lat")
    val user_lat: String,
    @SerializedName("user_long")
    val user_long: String,
    @SerializedName("users_id")
    val users_id: Int
): Serializable