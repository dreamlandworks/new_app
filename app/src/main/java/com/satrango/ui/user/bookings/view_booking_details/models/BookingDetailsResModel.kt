package com.satrango.ui.user.bookings.view_booking_details.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment
import java.io.Serializable

@Keep
data class BookingDetailsResModel(
    @SerializedName("attachments")
    val attachments: List<Attachment>,
    @SerializedName("booking_details")
    val booking_details: BookingDetails,
    @SerializedName("job_details")
    val job_details: List<JobDetail>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
): Serializable