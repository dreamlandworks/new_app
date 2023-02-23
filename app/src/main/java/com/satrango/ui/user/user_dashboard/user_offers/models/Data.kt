package com.satrango.ui.user.user_dashboard.user_offers.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("coupon_code")
    val coupon_code: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("offer_image")
    val offer_image: String,
    @SerializedName("offer_type_id")
    val offer_type_id: String,
    @SerializedName("offer_type_name")
    val offer_type_name: String,
    @SerializedName("valid_till")
    val valid_till: String,
    @SerializedName("value")
    val value: String,
    @SerializedName("value_type_id")
    val value_type_id: String,
    @SerializedName("value_type_name")
    val value_type_name: String
): Serializable