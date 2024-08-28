package com.oliver.wallet.data.model

import com.github.mikephil.charting.data.Entry
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.TypeMoney

data class MoneyUiState(
    val connectionState: ConnectionStatus = ConnectionStatus.Loading,
    val symbol: TypeMoney = TypeMoney.Dollar,
    val price: MoneyModel? = null,
    val chart: List<Entry>? = null,
    val calculate: Float = 1f,
){

    fun getCalculateResult(): Float {
        return (calculate * if (price?.bid == null) 1f else price.bid.toFloat())
    }
}
