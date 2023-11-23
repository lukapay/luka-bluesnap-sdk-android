package com.luka.sdk.bluesnap.lukasdk.presentation.viewmodel

import android.app.Activity
import android.content.Context
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluesnap.androidapi.models.BillingContactInfo
import com.bluesnap.androidapi.models.CreditCard
import com.bluesnap.androidapi.models.PurchaseDetails
import com.bluesnap.androidapi.services.BlueSnapService
import com.bluesnap.androidapi.services.BluesnapServiceCallback
import com.bluesnap.androidapi.services.CardinalManager
import com.luka.sdk.bluesnap.lukasdk.LukaBluesnap
import com.luka.sdk.bluesnap.lukasdk.Operation
import com.luka.sdk.bluesnap.lukasdk.di.UseCaseContainer
import com.luka.sdk.bluesnap.lukasdk.models.BluesnapResponse
import com.luka.sdk.bluesnap.lukasdk.models.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

internal class AddCardDetailsViewModel: ViewModel() {

    private val _card: MutableStateFlow<Card> = MutableStateFlow(Card())

    val card: StateFlow<Card> =
        _card.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Card())

    private val _endActivity = MutableStateFlow(false)
    private val _showErrorDialog = MutableStateFlow(false)
    private val _errorMsg = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(true)

    val endActivity: StateFlow<Boolean> = _endActivity.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val errorMsg: StateFlow<String> = _errorMsg.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")
    val showErrorDialog: StateFlow<Boolean> = _showErrorDialog.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val isLoading: StateFlow<Boolean> = _isLoading.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val cardNumberValue: StateFlow<TextFieldValue> =
        _card
            .map {
                TextFieldValue(
                    it.cardNumber,
                    selection = TextRange(max(it.cardNumber.length, 0))
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TextFieldValue(""))

    val cardExpiryDateValue: StateFlow<TextFieldValue> =
        _card
            .map {
                TextFieldValue(
                    it.expiryDate,
                    selection = TextRange(max(it.expiryDate.length, 0))
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TextFieldValue(""))

    val cardCvvValue: StateFlow<TextFieldValue> =
        _card
            .map { TextFieldValue(it.cvv, selection = TextRange(max(it.cvv.length, 0))) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TextFieldValue(""))

    val formatCardNumberUseCase = UseCaseContainer.formatCardNumberUseCase
    val formatExpiryDateUseCase = UseCaseContainer.formatExpiryDateUseCase
    val formatCvvUseCase = UseCaseContainer.formatCvvUseCase
    val addCardUseCase = UseCaseContainer.addCardsUseCase
    val getBluesnapToken = UseCaseContainer.getBluesnapToken

    val isValidCard: StateFlow<Boolean> =
        _card.map {
            it.cardNumber.isNotEmpty() &&
                    it.cardHolderName.isNotEmpty() &&
                    it.cvv.isNotEmpty() &&
                    it.expiryDate.isNotEmpty()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun showError(msg: String) {
        _showErrorDialog.value = true
        _errorMsg.value = msg
        _isLoading.value = false
    }

    fun dismissError() {
        _showErrorDialog.value = false
        _errorMsg.value = ""
    }

    fun updateCard(event: AddCardsUiEvent) {
        when (event) {
            is AddCardsUiEvent.UpdateCardHolderName -> {
                _card.update { it.copy(cardHolderName = event.value) }
            }

            is AddCardsUiEvent.UpdateCardNumber -> {
                val cardNumber = formatCardNumberUseCase(event.value.text)
                _card.update { it.copy(cardNumber = cardNumber) }
            }

            is AddCardsUiEvent.UpdateCvvNumber -> {
                val cvv = formatCvvUseCase(event.value.text)
                _card.update { it.copy(cvv = cvv) }
            }

            is AddCardsUiEvent.UpdateExpiryDate -> {
                val expiryDate = formatExpiryDateUseCase(event.value.text)
                _card.update { it.copy(expiryDate = expiryDate) }
            }

            is AddCardsUiEvent.Submit -> {
                processCard(activity = event.activity)
            }
        }
    }

    private fun processCard(activity: Activity) =
        viewModelScope.launch(Dispatchers.IO) {

            // verify card with bluesnap

            val request = PurchaseDetails(
                CreditCard().apply {
                    update(
                        card.value.cardNumber,
                        card.value.expiryDateFormatted,
                        card.value.cvv
                    )
                },
                BillingContactInfo(),
                true
            )

            val response = BlueSnapService.getInstance().submitTokenizedDetails(request)

            if (response.responseCode != 200) return@launch

            val body = BluesnapResponse.fromJson(response.responseString)

            val card = _card.value.copy(country = body?.issuingCountry ?: "", type = body?.ccType ?: "", subType = body?.cardSubType ?: "")

            CardinalManager.getInstance().authWith3DS(
                "USD",
                1.0,
                activity,
                CreditCard().apply {
                    update(
                        card.cardNumber,
                        card.expiryDateFormatted,
                        card.cvv
                    )
                })

            _isLoading.value = true

        }


    fun cont(email: String, customerId: String?) = viewModelScope.launch {
        val operation = (LukaBluesnap.instance.operation as? Operation.AddCard) ?: return@launch
        try {
            val result = addCardUseCase(email = email, customerId = customerId, card = _card.value)
            operation.successCall?.invoke(result)
            _endActivity.update { true }
        } catch (e: Exception) {
            operation.errorCall?.invoke(e)

        }
    }

    fun setUpBluesnap(context: Context) =
        viewModelScope.launch {
            val token = getBluesnapToken()
            BlueSnapService.getInstance().setup(
                token,
                { tokenServiceCallback -> tokenServiceCallback?.complete(token) },
                context,
                object : BluesnapServiceCallback {
                    override fun onSuccess() {
                        println("success setting up bluesnap")
                    }

                    override fun onFailure() {
                        _endActivity.update { true }
                    }

                }
            )
    }
}

sealed interface AddCardsUiEvent {
    class UpdateCardNumber(val value: TextFieldValue) : AddCardsUiEvent
    class UpdateCvvNumber(val value: TextFieldValue) : AddCardsUiEvent
    class UpdateExpiryDate(val value: TextFieldValue) : AddCardsUiEvent
    class UpdateCardHolderName(val value: String) : AddCardsUiEvent
    class Submit(val activity: Activity, val email: String, val customerId: String?): AddCardsUiEvent
}