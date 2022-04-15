package com.satrango.ui.user.user_dashboard.chats.models

data class ChatMessageModel(
    val message: String,
    val datetime: String,
    val sentBy: String,
    val type: String,
    val unseen: String
)