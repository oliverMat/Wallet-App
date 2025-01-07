package com.oliver.wallet.data.room.repositorio

import com.oliver.wallet.data.room.dao.TaxDao
import com.oliver.wallet.data.room.model.TaxModel
import com.oliver.wallet.data.room.repositorio.inter.TaxRepository
import kotlinx.coroutines.flow.Flow

class TaxRepo(private val taxDao: TaxDao) : TaxRepository {

    override suspend fun update(taxModel: TaxModel) = taxDao.update(taxModel)

    override fun getAllTaxStream(): Flow<TaxModel> = taxDao.getAll()
}