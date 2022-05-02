package com.satrango.ui.user.bookings.booking_attachments.models

data class ViewFilesResModel(
    val attachments: List<Attachments>,
    val description: String,
    val sent_by: String,
    val profile_pic: String,
    val username: String,
    val datetime: String,
) {
    constructor(): this(emptyList(), "", "", "", "", "")
}