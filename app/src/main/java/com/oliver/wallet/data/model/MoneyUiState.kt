package com.oliver.wallet.data.model

import com.github.mikephil.charting.data.Entry
import com.oliver.wallet.data.network.MoneyModel
import com.oliver.wallet.data.room.CoinModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.Constants.DAILY_STANDARD
import com.oliver.wallet.util.DateValueFormatter
import com.oliver.wallet.util.TypeMoney
import com.oliver.wallet.util.toDecimalFormatTreePlaces
import com.oliver.wallet.util.toDecimalFormatTwoPlaces

data class MoneyUiState(
    val connectionState: ConnectionStatus = ConnectionStatus.Loading,
    val typeMoney: TypeMoney = TypeMoney.Dollar,
    val price: MoneyModel? = null,
    val chart: List<Entry>? = null,
    val calculate: Float = 0f,
    val dailyChart: String = DAILY_STANDARD,
    val listCoin: List<CoinModel>? = null,
    val coin: CoinModel? = null
) {

    fun getCalculateResult(): String {
        return (calculate / if (price?.bid == null) 0f else price.bid.toFloat()).toDecimalFormatTwoPlaces()
    }

    fun getDateMinChart(): String {
        return DateValueFormatter().getAxisLabel(chart?.minByOrNull { it.y }!!.x, null)
    }

    fun getMinYDecimalChart(): String? {
        return chart?.minByOrNull { it.y }?.y?.toDecimalFormatTreePlaces()
    }

    fun getDateMaxChart(): String {
        return DateValueFormatter().getAxisLabel(chart?.maxByOrNull { it.y }!!.x, null)
    }

    fun getMaxYDecimalChart(): String? {
        return chart?.maxByOrNull { it.y }?.y?.toDecimalFormatTreePlaces()
    }
}
