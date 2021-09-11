package com.satrango.ui.user.bookings.view_booking_details.models

import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment

data class BookingDetailsResModel(
    val attachments: List<Attachment>,
    val booking_details: BookingDetails,
    val job_details: List<JobDetail>,
    val message: String,
    val status: Int
)