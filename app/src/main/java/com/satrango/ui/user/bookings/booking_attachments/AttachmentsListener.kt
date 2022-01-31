package com.satrango.ui.user.bookings.booking_attachments

import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment

interface AttachmentsListener {

    fun deleteAttachment(position: Int, imagePath: Attachment)

}