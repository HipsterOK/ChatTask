package ru.porcupine.chattask.data.model

data class Message(
    val id: String,
    val userId: String,
    val content: String,
    val timestamp: Long
)
