package com.oliver.wallet.data.room

import kotlinx.coroutines.flow.Flow

class CoinRepo(private val coinDao: CoinDao) : CoinRepository {

    override suspend fun update(coinModel: CoinModel) = coinDao.update(coinModel)

    override fun getAllCoinStream(): Flow<List<CoinModel>> = coinDao.getAll()

    override suspend fun getFavoriteCoin(): CoinModel = coinDao.getFavoriteCoin()
}