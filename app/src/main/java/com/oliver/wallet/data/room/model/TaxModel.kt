package com.oliver.wallet.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tax")
data class TaxModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val iof: Float,
    val taxa: Float
)
