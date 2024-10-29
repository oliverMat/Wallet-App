package com.oliver.wallet.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin")
data class CoinModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val label: String,
    var image: String,
    var favorite: Boolean
)
