package com.oliver.wallet.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.oliver.wallet.data.room.model.TaxModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaxDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(taxModel: TaxModel)

    @Update
    suspend fun update(taxModel: TaxModel)

    @Query("SELECT * from tax WHERE id = 1")
    fun getAll(): Flow<TaxModel>
}