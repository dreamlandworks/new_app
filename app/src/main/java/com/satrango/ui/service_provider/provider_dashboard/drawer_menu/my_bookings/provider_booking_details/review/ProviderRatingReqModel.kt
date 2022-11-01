package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ProviderRatingReqModel(
    @SerializedName("overall_rating")
    val overall_rating: Float,
    @SerializedName("professionalism")
    val professionalism: Float,
    @SerializedName("skill")
    val skill: Float,
    @SerializedName("behaviour")
    val behaviour: Float,
    @SerializedName("satisfaction")
    val satisfaction: Float,
    @SerializedName("feedback")
    val feedback: String,
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("sp_id")
    val sp_id: Int,
    @SerializedName("key")
    val key: String
): Serializable