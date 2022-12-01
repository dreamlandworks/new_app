package com.satrango.ui.user.bookings.view_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class GetQRCodeSPReqModel(
    @SerializedName("address")
    val address: String,
    @SerializedName("cat_id")
    val cat_id: Int,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("offer_id")
    val offer_id: Int,
    @SerializedName("postal_code")
    val postal_code: String,
    @SerializedName("profession_id")
    val profession_id: Int,
    @SerializedName("sp_id")
    val sp_id: Int,
    @SerializedName("state")
    val state: String,
    @SerializedName("user_lat")
    val user_lat: String,
    @SerializedName("user_long")
    val user_long: String,
    @SerializedName("users_id")
    val users_id: Int
): Serializable