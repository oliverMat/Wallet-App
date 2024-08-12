package com.oliver.wallet.data.network.money


import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.data.model.MoneyResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MoneyService {

    @GET("json/last/{symbol}")
    suspend fun getCurrentCoinData(@Path("symbol") symbol: String): MoneyResponse

    @GET("json/daily/{symbol}/7")
    suspend fun getCoinRatesData(@Path("symbol") symbol: String): List<MoneyModel>
}