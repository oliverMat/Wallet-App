package com.oliver.wallet.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.oliver.wallet.data.room.model.RatesModel
import kotlinx.coroutines.flow.Flow

@Dao
interface RatesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ratesModel: RatesModel)

    @Update
    suspend fun update(ratesModel: RatesModel)

    @Query("SELECT * from rates WHERE id = 1")
    fun getAll(): Flow<RatesModel>
}