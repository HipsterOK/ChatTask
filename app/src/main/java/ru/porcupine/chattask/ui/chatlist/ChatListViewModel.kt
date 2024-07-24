package ru.porcupine.chattask.ui.chatlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.porcupine.chattask.data.model.Chat
import ru.porcupine.chattask.data.network.DummyDataProvider

class ChatListViewModel : ViewModel() {

    private val _chatList = MutableLiveData<List<Chat>>()
    val chatList: LiveData<List<Chat>> get() = _chatList

    init {
        loadChats()
    }

    private fun loadChats() {
        _chatList.value = DummyDataProvider.getChats()
    }
}
