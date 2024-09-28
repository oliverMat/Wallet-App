package com.oliver.wallet.data.network.money


import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.data.model.MoneyResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MoneyService {

    @GET("json/last/{symbol}")
    suspend fun getCurrentCoinData(@Path("symbol") symbol: String): MoneyResponse

    @GET("json/daily/{symbol}/{daily}")
    suspend fun getCoinDaily(@Path("symbol") symbol: String, @Path("daily") daily: String): List<MoneyModel>
}