package com.luka.sdk.bluesnap.lukasdk

import com.luka.sdk.bluesnap.lukasdk.usecases.FormatCardNumberUseCase
import org.junit.Test

import org.junit.Assert.*


class FormatCardNumberUseCaseTest {
    @Test
    fun test_formatCardNumberUseCase() {
        val useCase = FormatCardNumberUseCase()
//        assertEquals(useCase("4000"), "4000")
//        assertEquals(useCase("40000"), "4000 0")
//        assertEquals(useCase("4000000000"), "4000 0000 00")
//        assertEquals(useCase("40000000000"), "4000 0000 000")
        assertEquals("4", useCase("4."))
    }
}