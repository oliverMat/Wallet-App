package com.oliver.wallet.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.oliver.wallet.data.room.model.CoinModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(coinModel: CoinModel)

    @Update
    suspend fun update(coinModel: CoinModel)

    @Query("SELECT * from coin ORDER BY label ASC")
    fun getAll(): Flow<List<CoinModel>>

    @Query("SELECT * FROM coin WHERE isFavorite = 1 LIMIT 1;")
    suspend fun getFavoriteCoin(): CoinModel
}