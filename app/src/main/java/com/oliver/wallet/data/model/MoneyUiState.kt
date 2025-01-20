package com.oliver.wallet.data.model

import com.github.mikephil.charting.data.Entry
import com.oliver.wallet.data.network.MoneyModel
import com.oliver.wallet.data.room.model.CoinModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.Constants.DAILY_STANDARD
import com.oliver.wallet.util.TypeMoney
import com.oliver.wallet.util.toDecimalFormatTwoPlaces

data class MoneyUiState(
    val connectionState: ConnectionStatus = ConnectionStatus.Loading,
    val typeMoney: TypeMoney = TypeMoney.Dollar,
    val price: MoneyModel? = null,
    val chart: List<Entry>? = null,
    val calculate: CalculatorModel = CalculatorModel(),
    val dailyChart: String = DAILY_STANDARD,
    val listCoin: List<CoinModel>? = null,
    val coin: CoinModel? = null
) {

    private fun getPrice(): Float {
        return (price?.bid ?: "0").toFloat()
    }

    fun getCalculateResult(): String {
        val value = getPrice() + getIof() + getTaxa()

        return (calculate.value / value).toDecimalFormatTwoPlaces()
    }

    fun getIof(): Float {
        return calculate.iof.times(getPrice())
    }

    fun getTaxa(): Float {
        return calculate.spread.times(getPrice())
    }

    fun getResultsWithAllTax(): Float {
        return (getPrice() + getIof() + getTaxa())
    }
}
