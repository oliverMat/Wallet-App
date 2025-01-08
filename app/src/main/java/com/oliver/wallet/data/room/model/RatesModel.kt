package com.oliver.wallet.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rates")
data class RatesModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val iof: Float,
    val spread: Float
)
