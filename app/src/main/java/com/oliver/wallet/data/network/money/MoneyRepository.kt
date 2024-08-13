package com.oliver.wallet.data.network.money

import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.data.model.MoneyResponse
import com.oliver.wallet.data.network.ResultWrapper
import com.oliver.wallet.data.network.RetrofitInstance.api_money
import com.oliver.wallet.data.network.safeApiCall

class MoneyRepository {

    suspend fun getCurrentCoinData(symbol: String): ResultWrapper<MoneyResponse> {
        return safeApiCall {
            api_money.getCurrentCoinData(symbol)
        }
    }

    suspend fun getCoinRatesData(symbol: String): ResultWrapper<List<MoneyModel>> {
        return safeApiCall {
            api_money.getCoinRatesData(symbol)
        }
    }
}