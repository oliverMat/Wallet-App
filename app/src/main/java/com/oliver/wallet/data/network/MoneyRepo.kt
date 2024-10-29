package com.oliver.wallet.data.network

class MoneyRepo(private val apiMoney: MoneyService): MoneyRepository {

    override suspend fun getCurrentCoinData(symbol: String): ResultWrapper<MoneyResponse> {
        return safeApiCall {
            apiMoney.getCurrentCoinData(symbol)
        }
    }

    override suspend fun getCoinDaily(symbol: String, daily: String): ResultWrapper<List<MoneyModel>> {
        return safeApiCall {
            apiMoney.getCoinDaily(symbol, daily)
        }
    }
}