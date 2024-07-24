package ru.porcupine.chattask.data.network

import ru.porcupine.chattask.data.model.Chat
import ru.porcupine.chattask.data.model.Message

object DummyDataProvider {

    fun getChats(): List<Chat> {
        return listOf(
            Chat("1", "Alice", "Hello!", "10:30 AM"),
            Chat("2", "Bob", "How are you?", "11:00 AM"),
            Chat("3", "Charlie", "Let's meet up.", "1:00 PM")
        )
    }

    fun getMessages(chatId: String): List<Message> {
        return when (chatId) {
            "1" -> listOf(
                Message("1", "user1", "Hi!", System.currentTimeMillis()),
                Message("2", "user2", "Hello!", System.currentTimeMillis())
            )
            "2" -> listOf(
                Message("3", "user1", "Hey!", System.currentTimeMillis()),
                Message("4", "user2", "I'm good, thanks!", System.currentTimeMillis())
            )
            "3" -> listOf(
                Message("5", "user1", "Sure, when?", System.currentTimeMillis()),
                Message("6", "user2", "How about 3 PM?", System.currentTimeMillis())
            )
            else -> emptyList()
        }
    }

}
