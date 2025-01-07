package com.oliver.wallet.data.room.repositorio.inter

import com.oliver.wallet.data.room.model.TaxModel
import kotlinx.coroutines.flow.Flow

interface TaxRepository {

    suspend fun update(taxModel : TaxModel)

    fun getAllTaxStream(): Flow<TaxModel>
}