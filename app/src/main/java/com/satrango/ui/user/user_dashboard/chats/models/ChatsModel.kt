package com.satrango.ui.user.user_dashboard.chats.models

data class ChatsModel(
    val chat_branch: String,
    val profile_image: String,
    val user_id: String,
    val username: String,
    val last_message: String,
    val sent_by: String,
    val datetime: Long
) {
    constructor(): this("", "", "", "", "", "", 0)
}