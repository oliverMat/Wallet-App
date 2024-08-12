package com.oliver.wallet.data.network.money

import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.data.model.MoneyResponse
import com.oliver.wallet.data.network.RetrofitInstance

class MoneyRepository {

    suspend fun getCurrentCoinData(symbol: String): MoneyResponse {
        return RetrofitInstance.api_money.getCurrentCoinData(symbol)
    }

    suspend fun getCoinRatesData(symbol: String): List<MoneyModel> {
        return RetrofitInstance.api_money.getCoinRatesData(symbol)
    }
}