package com.satrango.ui.user.user_dashboard.user_offers.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class OffersListReqModel(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("offer_type_id")
    val offer_type_id: Int,
    @SerializedName("postal_code")
    val postal_code: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("users_id")
    val users_id: Int,
    @SerializedName("sort_type")
    val sort_type: String
): Serializable