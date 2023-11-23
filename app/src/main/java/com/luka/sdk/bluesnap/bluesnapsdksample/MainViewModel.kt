package com.luka.sdk.bluesnap.bluesnapsdksample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luka.sdk.bluesnap.lukasdk.LukaBluesnap
import com.luka.sdk.bluesnap.lukasdk.models.LukaCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel() {

    private val _cards: MutableStateFlow<List<LukaCard>> = MutableStateFlow(emptyList())

    val cards: StateFlow<List<LukaCard>> =
        _cards.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getCards() {
        LukaBluesnap.getCards(clientId = "e1555a98-881a-48a5-b958-fc1c6f37f258")
            .onSuccess { list ->
                _cards.update { list }
            }.start()
    }
}