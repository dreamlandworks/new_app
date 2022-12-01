package com.satrango.ui.service_provider.provider_dashboard.plans.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderPlansResModel(
    @SerializedName("activated_plan")
    val activated_plan: Int,
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable