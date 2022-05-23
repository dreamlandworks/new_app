package com.satrango.ui.user.bookings.booking_address.models

import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Attachment

data class SingleMoveBookingReqModel(
    val address_id: Int,
    val amount: String,
    val attachments: List<Attachment>,
    val created_on: String,
    val estimate_time: Int,
    val estimate_type_id: Int,
    val job_description: String,
    val key: String,
    val scheduled_date: String,
    val sp_id: Int,
    val started_at: String,
    val temp_address_id: Int,
    val time_slot_from: String,
    val time_slot_to: String,
    val users_id: Int,
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val postal_code: String,
    val user_lat: String,
    val user_long: String,
    val cgst: String,
    val sgst: String,
    val profession_id: String
    )