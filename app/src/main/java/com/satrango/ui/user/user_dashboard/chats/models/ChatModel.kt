package com.satrango.ui.user.user_dashboard.chats.models

data class ChatModel(
    val datetime: Long,
    val online_status: String,
    val profile_image: String,
    val user_id: String,
    val username: String
) {
    constructor() : this(0, "","", "", "")
}