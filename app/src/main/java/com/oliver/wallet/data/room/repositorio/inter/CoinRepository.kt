package com.oliver.wallet.data.room.repositorio.inter

import com.oliver.wallet.data.room.model.CoinModel
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    suspend fun update(coinModel : CoinModel)

    fun getAllCoinStream(): Flow<List<CoinModel>>

    suspend fun getFavoriteCoin(): CoinModel
}