package com.oliver.wallet.data.model

import com.google.gson.annotations.SerializedName

data class MoneyResponse(
        @SerializedName("USDBRL") val dollar: MoneyModel,
        @SerializedName("EURBRL") val euro: MoneyModel
)

data class MoneyModel(
        val code: String?,
        val codein: String?,
        val name: String?,
        val high: String?,
        val low: String?,
        val varBid: String?,
        val pctChange: String?,
        val bid: String?,
        val ask: String?,
        val timestamp: String?,
        val create_date: String?
)
