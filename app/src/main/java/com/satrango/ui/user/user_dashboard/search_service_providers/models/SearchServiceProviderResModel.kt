package com.satrango.ui.user.user_dashboard.search_service_providers.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class SearchServiceProviderResModel(
    @SerializedName("charges")
    val charges: List<Charge>,
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("wallet_balance")
    val wallet_balance: Int,
    @SerializedName("search_results_id")
    val search_results_id: Int,
    @SerializedName("slots_data")
    val slots_data: List<SlotsData>,
    @SerializedName("status")
    val status: Int,
    @SerializedName("temp_address_id")
    val temp_address_id: Int
): Serializable