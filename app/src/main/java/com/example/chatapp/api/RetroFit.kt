package com.example.chatapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object Retrofit {
    private const val BASE_URL = "" // Replace with your actual base URL
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }
}