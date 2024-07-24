package ru.porcupine.chattask.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.porcupine.chattask.data.network.DummyDataProvider
import ru.porcupine.chattask.data.model.Message

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    fun getMessages(chatId: String) {
        _messages.value = DummyDataProvider.getMessages(chatId)
    }

    fun sendMessage(content: String) {
        val newMessage = Message(
            id = "new", content = content, timestamp = System.currentTimeMillis(), userId = "0"
        )
        _messages.value = _messages.value.orEmpty() + newMessage
    }
}
