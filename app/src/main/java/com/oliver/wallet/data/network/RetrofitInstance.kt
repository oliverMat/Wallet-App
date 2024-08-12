package com.oliver.wallet.data.network

import com.oliver.wallet.data.network.money.MoneyService
import com.oliver.wallet.data.network.stockexchange.StockService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL_STOCK = "https://brapi.dev/api/"
    private const val BASE_URL_MONEY = "https://economia.awesomeapi.com.br/"

    private val client = OkHttpClient.Builder().build()

    private val retrofit_stock by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_STOCK)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api_stock: StockService by lazy {
        retrofit_stock.create(StockService::class.java)
    }

    private val retrofit_money by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_MONEY)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api_money: MoneyService by lazy {
        retrofit_money.create(MoneyService::class.java)
    }
}