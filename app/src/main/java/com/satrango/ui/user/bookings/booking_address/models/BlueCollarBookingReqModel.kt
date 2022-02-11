package com.satrango.ui.user.bookings.booking_address.models

import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment

data class BlueCollarBookingReqModel(
    val amount: String,
    val attachments: ArrayList<Attachment>,
    val created_on: String,
    val estimate_time: Int,
    val estimate_type_id: Int,
    val job_description: String,
    val key: String,
    val scheduled_date: String,
    val sp_id: Int,
    val started_at: String,
    val time_slot_from: String,
    val time_slot_to: String,
    val users_id: Int,
    val cgst: String,
    val sgst: String,
    val profession_id: String
)