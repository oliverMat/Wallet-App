package com.oliver.wallet.data.room.repositorio

import com.oliver.wallet.data.room.repositorio.inter.CoinRepository
import com.oliver.wallet.data.room.dao.CoinDao
import com.oliver.wallet.data.room.model.CoinModel
import kotlinx.coroutines.flow.Flow

class CoinRepo(private val coinDao: CoinDao) : CoinRepository {

    override suspend fun update(coinModel: CoinModel) = coinDao.update(coinModel)

    override fun getAllCoinStream(): Flow<List<CoinModel>> = coinDao.getAll()

    override suspend fun getFavoriteCoin(): CoinModel = coinDao.getFavoriteCoin()
}