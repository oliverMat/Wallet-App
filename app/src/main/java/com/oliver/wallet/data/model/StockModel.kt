package com.oliver.wallet.data.model

data class StockResponse(
    val results: List<StockModel>
)

data class StockModel(
    val currency: String,
    val shortName: String,
    val longName: String,
    val regularMarketChange: Double,
    val regularMarketChangePercent: Double,
    val regularMarketTime: String,
    val regularMarketPrice: Double,
    val regularMarketDayHigh: Double,
    val regularMarketDayRange: String,
    val regularMarketDayLow: Double,
    val regularMarketVolume: Long,
    val regularMarketPreviousClose: Double,
    val regularMarketOpen: Double,
    val fiftyTwoWeekRange: String,
    val fiftyTwoWeekLow: Double,
    val fiftyTwoWeekHigh: Double,
    val symbol: String,
    val priceEarnings: Double,
    val earningsPerShare: Double,
    val logourl: String
)

