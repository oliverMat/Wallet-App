package com.oliver.wallet.data.room.repositorio.inter

import com.oliver.wallet.data.room.model.RatesModel
import kotlinx.coroutines.flow.Flow

interface RatesRepository {

    suspend fun update(ratesModel : RatesModel)

    fun getAllRatesStream(): Flow<RatesModel>
}