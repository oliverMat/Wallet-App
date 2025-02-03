package com.oliver.wallet.data.network

import com.google.gson.annotations.SerializedName

data class MoneyResponse(
        @SerializedName("USDBRL") val dollar: MoneyModel,
        @SerializedName("EURBRL") val euro: MoneyModel,
        @SerializedName("BRLUSD") val brazilianReal: MoneyModel,
        @SerializedName("JPYBRL") val japaneseYen: MoneyModel,
        @SerializedName("GBPBRL") val poundSterling: MoneyModel,
        @SerializedName("AUDBRL") val australianDollar: MoneyModel,
        @SerializedName("CADBRL") val canadianDollar: MoneyModel,
        @SerializedName("CHFBRL") val swissFranc: MoneyModel,
        @SerializedName("CNYBRL") val chineseYuan: MoneyModel,
        @SerializedName("SEKBRL") val swedishKrona: MoneyModel,
        @SerializedName("NZDBRL") val newZealandDollar: MoneyModel,
        @SerializedName("MXNBRL") val mexicanPeso: MoneyModel,
        @SerializedName("SGDBRL") val singaporeDollar: MoneyModel,
        @SerializedName("HKDBRL") val hongKongDollar: MoneyModel,
        @SerializedName("NOKBRL") val norwegianKrone: MoneyModel,
        @SerializedName("TRYBRL") val turkishLira: MoneyModel,
        @SerializedName("INRBRL") val indianRupee: MoneyModel,
        @SerializedName("RUBBRL") val russianRuble: MoneyModel,
        @SerializedName("ZARBRL") val southAfricanRand: MoneyModel,
        @SerializedName("CLPBRL") val chileanPeso: MoneyModel,
        @SerializedName("ARSBRL") val argentinePeso: MoneyModel,
        @SerializedName("TWDBRL") val taiwanDollar: MoneyModel,
        @SerializedName("THBBRL") val thaiBaht: MoneyModel,
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
