package com.oliver.wallet.data.network.stockexchange

import com.oliver.wallet.data.model.StockResponse
import com.oliver.wallet.data.network.RetrofitInstance


class StockRepository {
    private val apiToken = "5MR1NcVq7vAKnXGDgNgZLC"

    suspend fun getStock(symbol: String): StockResponse {
        return RetrofitInstance.api_stock.getStock(symbol, apiToken)
    }

    suspend fun getIndexB3(): StockResponse {
        return RetrofitInstance.api_stock.getIndexB3(apiToken)
    }

    suspend fun getIndexSp500(): StockResponse {
        return RetrofitInstance.api_stock.getIndexSp500(apiToken)
    }
}