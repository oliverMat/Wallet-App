package com.oliver.wallet.data.network

interface MoneyRepository {

    suspend fun getCurrentCoinData(symbol: String): ResultWrapper<MoneyResponse>

    suspend fun getCoinDaily(symbol: String, daily: String): ResultWrapper<List<MoneyModel>>
}