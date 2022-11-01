package com.satrango.ui.service_provider.provider_dashboard

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderLocationReqModel(
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("online_status_id")
    val online_status_id: Int,
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