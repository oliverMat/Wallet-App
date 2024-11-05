package com.oliver.wallet.data.network


import retrofit2.http.GET
import retrofit2.http.Path

interface MoneyService {

    @GET("json/last/{symbol}")
    suspend fun getPriceOfDay(@Path("symbol") symbol: String): MoneyResponse

    @GET("json/daily/{symbol}/{daily}")
    suspend fun getChartForPeriod(@Path("symbol") symbol: String, @Path("daily") daily: String): List<MoneyModel>
}