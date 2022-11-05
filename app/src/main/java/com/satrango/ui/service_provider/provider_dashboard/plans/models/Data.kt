package com.satrango.ui.service_provider.provider_dashboard.plans.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("bids_per_month")
    val bids_per_month: String,
    @SerializedName("customer_support")
    val customer_support: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("period")
    val period: String,
    @SerializedName("platform_fee_per_booking")
    val platform_fee_per_booking: String,
    @SerializedName("premium_tag")
    val premium_tag: String,
    @SerializedName("sealed_bids_per_month")
    val sealed_bids_per_month: String
): Serializable