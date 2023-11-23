package com.luka.sdk.bluesnap.lukasdk.network

import com.luka.sdk.bluesnap.lukasdk.models.CardDto
import com.luka.sdk.bluesnap.lukasdk.models.Credentials
import com.luka.sdk.bluesnap.lukasdk.models.Transaction
import com.luka.sdk.bluesnap.lukasdk.models.TransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

internal interface LukaApi {

    @POST("servicio/login")
    suspend fun  login(@Body body: Credentials): Response<String>

    @GET("transaccion/token")
    suspend fun getBsToken(@Header("Authorization") lukaBearerToken: String) : Response<String>

    @POST("transaccion")
    suspend fun processPayment(@Header("Authorization") lukaBearerToken: String, @Body body: Transaction): Response<TransactionResponse>

    @GET("tarjetacredito/servicio/{customerId}")
    suspend fun getLukaCards(@Header("Authorization") lukaBearerToken: String, @Path("customerId") clientId: String) : List<CardDto>

    @DELETE("tarjetacredito/{cardId}/user/{customerId}")
    suspend fun deleteCard(@Header("Authorization") lukaBearerToken: String, @Path("cardId") cardId: Int, @Path("customerId") clientId: String) : Response<String>

}