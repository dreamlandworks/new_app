package com.satrango.ui.user.user_dashboard.search_service_providers.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class SearchServiceProviderReqModel(
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("search_phrase_id")
    val search_phrase_id: String,
    @SerializedName("search_phrase")
    val search_phrase: String,
    @SerializedName("postal_code")
    val postal_code: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("user_lat")
    val user_lat: String,
    @SerializedName("user_long")
    val user_long: String,
    @SerializedName("users_id")
    val users_id: Int,
    @SerializedName("subcat_id")
    val subcat_id: Int,
    @SerializedName("offer_id")
    val offer_id: Int
): Serializable