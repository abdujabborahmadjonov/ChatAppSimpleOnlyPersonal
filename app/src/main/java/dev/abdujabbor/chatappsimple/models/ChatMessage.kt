package dev.abdujabbor.chatappsimple.models
data class ChatMessage(
    val senderName:String="",
    var senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)