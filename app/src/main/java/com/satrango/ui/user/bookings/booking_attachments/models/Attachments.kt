package com.satrango.ui.user.bookings.booking_attachments.models

data class Attachments(
    val type: String,
    val url: String
) {
    constructor(): this("", "")
}