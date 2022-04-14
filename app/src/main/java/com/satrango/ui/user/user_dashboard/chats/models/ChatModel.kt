package com.satrango.ui.user.user_dashboard.chats.models

data class ChatModel(
    val userName: String,
    val mobileNo: String,
    val lastMessage: String,
    val profileImage: String,
    val unseenMessage: Int,
    val userId: String,
    val sentBy: String,
    val chats: List<ChatMessageModel>
)