package com.satrango.ui.user.bookings.view_booking_details.installments_request.models

data class PostApproveRejectReqModel(
    val booking_id: Int,
    val inst_id: Int,
    val key: String,
    val sp_id: Int,
    val status_id: Int,
    val users_id: Int
)