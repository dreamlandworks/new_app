package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class UserRatingReqModel(
    @SerializedName("app_review")
    val app_review: Float,
    @SerializedName("booking_id")
    val booking_id: Int,
    @SerializedName("booking_rating")
    val booking_rating: Float,
    @SerializedName("feedback")
    val feedback: String,
    @SerializedName("job_satisfaction")
    val job_satisfaction: Float,
    @SerializedName("key")
    val key: String,
    @SerializedName("overall_rating")
    val overall_rating: Float,
    @SerializedName("user_id")
    val user_id: Int,
    @SerializedName("user_rating")
    val user_rating: Float
): Serializable