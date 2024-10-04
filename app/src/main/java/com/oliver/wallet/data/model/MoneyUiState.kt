package com.oliver.wallet.data.model

import com.github.mikephil.charting.data.Entry
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.Constants.DAILY_STANDARD
import com.oliver.wallet.util.DateValueFormatter
import com.oliver.wallet.util.TypeMoney
import com.oliver.wallet.util.toDecimalFormat

data class MoneyUiState(
    val connectionState: ConnectionStatus = ConnectionStatus.Loading,
    val symbol: TypeMoney = TypeMoney.Dollar,
    val price: MoneyModel? = null,
    val chart: List<Entry>? = null,
    val calculate: Float = 1f,
    val dailyChart: String = DAILY_STANDARD
){

    fun getCalculateResult(): Float {
        return (calculate * if (price?.bid == null) 1f else price.bid.toFloat())
    }

    fun getDateMinChart(): String {
        return DateValueFormatter().getAxisLabel(chart?.minByOrNull { it.y }!!.x, null)
    }

    fun getMinYDecimalChart(): String? {
        return chart?.minByOrNull { it.y }?.y?.toDecimalFormat()
    }

    fun getDateMaxChart(): String {
        return DateValueFormatter().getAxisLabel(chart?.maxByOrNull { it.y }!!.x, null)
    }

    fun getMaxYDecimalChart(): String? {
        return chart?.maxByOrNull { it.y }?.y?.toDecimalFormat()
    }
}
