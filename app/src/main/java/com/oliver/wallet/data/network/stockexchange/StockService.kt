package com.oliver.wallet.data.network.stockexchange

import com.oliver.wallet.data.model.StockResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StockService {
    @GET("quote/{symbol}")
    suspend fun getStock(@Path("symbol") symbol: String, @Query("token") token: String): StockResponse

    @GET("quote/^BVSP")
    suspend fun getIndexB3(@Query("token") token: String): StockResponse

    @GET("quote/^GSPC")
    suspend fun getIndexSp500(@Query("token") token: String): StockResponse
}