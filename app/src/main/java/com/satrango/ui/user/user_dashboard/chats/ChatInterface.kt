package com.satrango.ui.user.user_dashboard.chats

interface ChatInterface {

    fun downloadFile(url: String, fileType: String)

    fun showFile(url: String, fileType: String)

}