package com.luka.sdk.bluesnap.lukasdk.usecases

import com.luka.sdk.bluesnap.lukasdk.data.LukaRepository
import com.luka.sdk.bluesnap.lukasdk.models.LukaCard
import com.luka.sdk.bluesnap.lukasdk.models.TransactionResult

internal class ProcessPaymentUseCase(
    private val repository: LukaRepository
) {

    suspend operator fun invoke(amount: Double, email: String, customerId: String, card: LukaCard) : TransactionResult {
        return repository.processPayment(amount, email, customerId, card)
    }
}