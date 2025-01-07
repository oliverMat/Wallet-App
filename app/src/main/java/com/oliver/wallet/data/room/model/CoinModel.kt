package com.oliver.wallet.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oliver.wallet.util.TypeMoney

@Entity(tableName = "coin")
data class CoinModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val label: String,
    var typeMoney: TypeMoney,
    var image: Int,
    var isFavorite: Boolean
)
