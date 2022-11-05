package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderReviewResModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("sp_reviews")
    val sp_reviews: List<SpReview>,
    @SerializedName("status")
    val status: Int
): Serializable