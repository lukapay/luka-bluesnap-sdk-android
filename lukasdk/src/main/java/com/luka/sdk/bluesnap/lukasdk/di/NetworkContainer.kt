package com.luka.sdk.bluesnap.lukasdk.di

import com.luka.sdk.bluesnap.lukasdk.Environment
import com.luka.sdk.bluesnap.lukasdk.LukaBluesnap
import com.luka.sdk.bluesnap.lukasdk.network.LukaApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

internal object NetworkContainer {

    private val moshi:  Moshi
        get() = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    private val httpClient: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()

    private val url: String
        get() =
            if (LukaBluesnap.instance.config.env == Environment.Sandbox)
                "https://bspaycoapi-qa.payco.net.ve/api/v1/"
            else "https://lukaapi.payco.net.ve/api/v1"


    private val retrofit: Retrofit
        get() =
            Retrofit.Builder()
                .baseUrl(url)
                .client(httpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(
                    MoshiConverterFactory.create(moshi)
                )
                .build()

    val lukaApi: LukaApi
        get() = retrofit.create(LukaApi::class.java)
}