package com.oliver.wallet.data.room.repositorio

import com.oliver.wallet.data.room.dao.RatesDao
import com.oliver.wallet.data.room.model.RatesModel
import com.oliver.wallet.data.room.repositorio.inter.RatesRepository
import kotlinx.coroutines.flow.Flow

class RatesRepo(private val ratesDao: RatesDao) : RatesRepository {

    override suspend fun update(ratesModel: RatesModel) = ratesDao.update(ratesModel)

    override fun getAllRatesStream(): Flow<RatesModel> = ratesDao.getAll()
}