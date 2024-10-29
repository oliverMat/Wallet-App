package com.oliver.wallet.data.room

import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    suspend fun update(coinModel : CoinModel)

    fun getAllCoinStream(): Flow<List<CoinModel>>
}