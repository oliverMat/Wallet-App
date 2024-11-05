package com.oliver.wallet.data.network

class MoneyRepo(private val apiMoney: MoneyService): MoneyRepository {

    override suspend fun getPriceOfDay(symbol: String): ResultWrapper<MoneyResponse> {
        return safeApiCall {
            apiMoney.getPriceOfDay(symbol)
        }
    }

    override suspend fun getChartForPeriod(symbol: String, daily: String): ResultWrapper<List<MoneyModel>> {
        return safeApiCall {
            apiMoney.getChartForPeriod(symbol, daily)
        }
    }
}