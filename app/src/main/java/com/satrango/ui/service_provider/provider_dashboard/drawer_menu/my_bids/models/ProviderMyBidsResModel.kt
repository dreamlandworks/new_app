package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderMyBidsResModel(
    @SerializedName("job_post_details")
    val job_post_details: List<JobPostDetail>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable