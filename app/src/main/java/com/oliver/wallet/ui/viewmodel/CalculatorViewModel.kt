package com.oliver.wallet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.data.model.MoneyResponse
import com.oliver.wallet.data.network.ResultWrapper
import com.oliver.wallet.data.network.money.MoneyRepository
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.Constants.UPDATE_INTERVAL_30
import com.oliver.wallet.util.TypeMoney
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CalculatorViewModel : ViewModel() {
    private val moneyRepository = MoneyRepository()

    private val _connectionState = MutableStateFlow(ConnectionStatus.Loading)
    val connectionState: StateFlow<ConnectionStatus> = _connectionState

    private val _priceState = MutableStateFlow<MoneyModel?>(null)
    val priceState: StateFlow<MoneyModel?> = _priceState

    private val _calculateState = MutableStateFlow<Float?>(null)
    val calculateState: StateFlow<Float?> = _calculateState

    private var symbolMoney: TypeMoney = TypeMoney.Dollar
    private var calculationValue: Float = 1f

    init {
        loadPeriodically()
    }

    fun calculate(value: String) {
        val price = _priceState.value!!.bid
        val valueInsert = if (value.isEmpty()) 1f else value.toFloat()

        _calculateState.value = price.toFloat() * valueInsert
        calculationValue = valueInsert
    }

    private fun loadPeriodically() {
        viewModelScope.launch {
            while (isActive) {
                getCurrentCoinData(symbolMoney)
                delay(UPDATE_INTERVAL_30)
            }
        }
    }

    private suspend fun getCurrentCoinData(symbolMoney: TypeMoney) {
        when (val result = moneyRepository.getCurrentCoinData(symbolMoney.moneyType)) {
            is ResultWrapper.NetworkError -> {
                setLoadingStatus()
            }

            is ResultWrapper.GenericError -> {
                println(result.code)
                setErrorStatus()
            }

            is ResultWrapper.Success -> {
                setResponseMoney(result.value, symbolMoney)
                setSuccessStatus()
            }
        }
    }

    private fun setResponseMoney(response: MoneyResponse, symbolMoney: TypeMoney) {
         val value = when (symbolMoney) {
            TypeMoney.Dollar -> response.dollar
            TypeMoney.Euro -> response.euro
            TypeMoney.Bitcoin -> response.bitcoin
        }

        _priceState.value = value
        _calculateState.value = value.bid.toFloat() * calculationValue
    }

    private fun setSuccessStatus() {
        _connectionState.value = ConnectionStatus.Success
    }

    private fun setLoadingStatus() {
        _connectionState.value = ConnectionStatus.Loading
    }

    private fun setErrorStatus() {
        _connectionState.value = ConnectionStatus.Error
    }
}