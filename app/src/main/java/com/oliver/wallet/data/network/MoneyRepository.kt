package com.oliver.wallet.data.network

interface MoneyRepository {

    suspend fun getPriceOfDay(symbol: String): ResultWrapper<MoneyResponse>

    suspend fun getChartForPeriod(symbol: String, daily: String): ResultWrapper<List<MoneyModel>>
}