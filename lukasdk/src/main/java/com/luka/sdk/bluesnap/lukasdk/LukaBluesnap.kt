package com.luka.sdk.bluesnap.lukasdk

import android.content.Context
import android.content.Intent
import com.luka.sdk.bluesnap.lukasdk.di.UseCaseContainer
import com.luka.sdk.bluesnap.lukasdk.models.AddCardResult
import com.luka.sdk.bluesnap.lukasdk.models.LukaCard
import com.luka.sdk.bluesnap.lukasdk.models.TransactionResult
import com.luka.sdk.bluesnap.lukasdk.presentation.view.AddCardDetailsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LukaBluesnap {

    internal var config: Config = Config.Default
    internal var session: Session = Session.Default

    internal var operation: (Operation<*>)? = null

    companion object {
        internal val instance = LukaBluesnap()

        private val useCaseContainer = UseCaseContainer

        internal val scope = CoroutineScope(Dispatchers.Main)

        fun setUpConfig(config: Config, callbacks: Config.Callbacks) {
            instance.config = config
            scope.launch {
                try {
                    useCaseContainer.lukaAuthUseCase(config.credentials.username, config.credentials.password)
                    callbacks.onSuccess()
                }catch (e: Exception){
                    e.printStackTrace()
                    callbacks.onError()
                }

            }
        }

        fun processPayment(
            clientId: String,
            card: LukaCard,
            amount: Double,
            email: String
        ) : Operation.Payment {
            return Operation.Payment(
                customerId = clientId,
                card = card,
                amount = amount,
                email = email
            )
        }

        fun getCards(
            clientId: String
        ) : Operation.GetCards {
            return Operation.GetCards(
                customerId = clientId
            )
        }

        fun deleteCard(
            clientId: String,
            cardId: Int
        ) : Operation.DeleteCard {
            return Operation.DeleteCard(
                customerId = clientId,
                cardId = cardId
            )
        }

        fun addNewCard(
            context: Context,
            email: String,
            customerId: String? = null
        ) : Operation.AddCard {
            return Operation.AddCard(
                context = context,
                email = email,
                lukaCustomerId = customerId
            )
        }
    }
}

sealed class Operation<G> {

    internal var successCall : ((G) -> Unit)? = null
    internal var errorCall: ((Throwable) -> Unit)? = null
    internal var loadingCall: (() -> Unit)? = null

    fun onSuccess(call: (G) -> Unit): Operation<G> {
        this.successCall = call
        return this
    }

    fun onError(call: (Throwable) -> Unit): Operation<G> {
        this.errorCall = call
        return this
    }
    fun onLoading(call: () -> Unit): Operation<G> {
        this.loadingCall = call
        return this
    }

    abstract fun start()

    class GetCards(val customerId: String) : Operation<List<LukaCard>>() {
        override fun start() {
            LukaBluesnap.instance.operation = this
            LukaBluesnap.scope.launch {
                this@GetCards.loadingCall?.invoke()
                try {
                    val cards = UseCaseContainer.listCardUseCase(customerId)
                    this@GetCards.successCall?.invoke(cards)
                }catch (e: Exception) {
                    e.printStackTrace()
                    this@GetCards.successCall?.invoke(listOf())
                }

            }
        }
    }

    class AddCard(val context: Context, val email: String, val lukaCustomerId: String?) : Operation<AddCardResult>() {
        override fun start() {
            LukaBluesnap.instance.operation = this
            this@AddCard.loadingCall?.invoke()
            context.startActivity(
                Intent(
                    context, AddCardDetailsActivity::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("email", email)
                    putExtra("customerId", lukaCustomerId)
                }
            )
        }
    }

    class DeleteCard(val customerId: String, val cardId: Int) : Operation<Boolean>() {
        override fun start() {
            LukaBluesnap.instance.operation = this
            LukaBluesnap.scope.launch {
                this@DeleteCard.loadingCall?.invoke()
                try {
                    UseCaseContainer.deleteCardUseCase(customerId, cardId)
                    this@DeleteCard.successCall?.invoke(true)
                }catch (e:Exception) {
                    e.printStackTrace()
                    this@DeleteCard.successCall?.invoke(false)
                }

            }

        }
    }

    class Payment(
        val customerId: String,
        val card: LukaCard,
        val amount: Double,
        val email: String
    ) : Operation<TransactionResult>() {
        override fun start() {
            LukaBluesnap.instance.operation = this
            LukaBluesnap.scope.launch {
                this@Payment.loadingCall?.invoke()
                try {
                    val result = UseCaseContainer.processPaymentUseCase(amount, email, customerId, card)
                    this@Payment.successCall?.invoke(result)
                }catch (e:Exception) {
                    e.printStackTrace()
                    this@Payment.errorCall?.invoke(e)
                }

            }
        }
    }

}



sealed class Session (
    var lukaCustomerId: String = String.empty,
    var bsToken: String = String.empty,
    var lukaToken: String = String.empty,
    var clienId: String = String.empty
) {
    data object Default : Session()
}

enum class Environment {
    Sandbox,
    Live
}

open class Config(
    val env: Environment = Environment.Sandbox,
    val credentials: Credentials = Credentials(String.empty, String.empty)
) {
    data object Default : Config()

    public interface Callbacks {
        fun onSuccess()
        fun onError()
    }
}

data class Credentials(
    val username: String,
    val password: String
)

private val String.Companion.empty: String
    get() = ""