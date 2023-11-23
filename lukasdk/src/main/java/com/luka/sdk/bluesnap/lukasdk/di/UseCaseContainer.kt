package com.luka.sdk.bluesnap.lukasdk.di

import com.luka.sdk.bluesnap.lukasdk.usecases.AddCardUseCase
import com.luka.sdk.bluesnap.lukasdk.usecases.DeleteCardUseCase
import com.luka.sdk.bluesnap.lukasdk.usecases.FormatCardNumberUseCase
import com.luka.sdk.bluesnap.lukasdk.usecases.FormatCvvUseCase
import com.luka.sdk.bluesnap.lukasdk.usecases.FormatExpiryDateUseCase
import com.luka.sdk.bluesnap.lukasdk.usecases.GetBsTokenUseCase
import com.luka.sdk.bluesnap.lukasdk.usecases.ListCardsUseCase
import com.luka.sdk.bluesnap.lukasdk.usecases.LukaAuthUseCase
import com.luka.sdk.bluesnap.lukasdk.usecases.ProcessPaymentUseCase

internal object UseCaseContainer {

    private val lukaRepository = DataContainer.lukaRepository

    val lukaAuthUseCase: LukaAuthUseCase
        get() = LukaAuthUseCase(lukaRepository)

    val listCardUseCase: ListCardsUseCase
        get() = ListCardsUseCase(lukaRepository)

    val addCardsUseCase: AddCardUseCase
        get() = AddCardUseCase(lukaRepository)

    val getBluesnapToken: GetBsTokenUseCase
        get() = GetBsTokenUseCase(lukaRepository)

    val processPaymentUseCase: ProcessPaymentUseCase
        get() = ProcessPaymentUseCase(lukaRepository)

    val deleteCardUseCase: DeleteCardUseCase
        get() = DeleteCardUseCase(lukaRepository)

    val formatCardNumberUseCase: FormatCardNumberUseCase
        get() = FormatCardNumberUseCase()

    val formatExpiryDateUseCase: FormatExpiryDateUseCase
        get() = FormatExpiryDateUseCase()

    val formatCvvUseCase: FormatCvvUseCase
        get() = FormatCvvUseCase()
}