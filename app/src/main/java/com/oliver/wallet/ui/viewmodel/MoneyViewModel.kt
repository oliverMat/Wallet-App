package com.oliver.wallet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.data.model.MoneyResponse
import com.oliver.wallet.data.network.ResultWrapper
import com.oliver.wallet.data.network.money.MoneyRepository
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.Constants.UPDATE_INTERVAL_1
import com.oliver.wallet.util.Constants.UPDATE_INTERVAL_30
import com.oliver.wallet.util.TypeMoney
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MoneyViewModel : ViewModel() {
    private val moneyRepository = MoneyRepository()

    private val _price = MutableStateFlow<MoneyModel?>(null)
    val price: StateFlow<MoneyModel?> = _price

    private val _moneyChart = MutableStateFlow<List<Entry>?>(null)
    val moneyChart: StateFlow<List<Entry>?> = _moneyChart

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.Loading)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus

    private var symbolMoney: TypeMoney = TypeMoney.Dollar


    init {
        loadPeriodically()
    }

    private fun loadPeriodically() {
        viewModelScope.launch {
            while (isActive) {
                getCurrentCoinData(symbolMoney)
                getCoinRatesData(symbolMoney)
                delay(UPDATE_INTERVAL_30)
            }
        }
    }

    fun selectMoneySymbol(symbolMoney: TypeMoney) {
        this.symbolMoney = symbolMoney
        setLoadingStatus()
        setCurrentMoney(symbolMoney)
        setListMoney(symbolMoney)
    }

    private fun setCurrentMoney(symbolMoney: TypeMoney) {
        viewModelScope.launch {
            delay(UPDATE_INTERVAL_1)
            getCurrentCoinData(symbolMoney)
        }
    }

    private fun setListMoney(symbolMoney: TypeMoney) {
        viewModelScope.launch {
            delay(UPDATE_INTERVAL_1)
            getCoinRatesData(symbolMoney)
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
        _price.value = when (symbolMoney) {
            TypeMoney.Dollar -> response.dollar
            TypeMoney.Euro -> response.euro
            TypeMoney.Bitcoin -> response.bitcoin
        }
    }

    private suspend fun getCoinRatesData(symbolMoney: TypeMoney) {
        when (val result = moneyRepository.getCoinRatesData(symbolMoney.moneyType)) {
            is ResultWrapper.NetworkError -> {
                setLoadingStatus()
            }

            is ResultWrapper.GenericError -> {
                println(result.code)
                setErrorStatus()
            }

            is ResultWrapper.Success -> {
                _moneyChart.value = result.value.mapIndexed { index, it ->
                    Entry(index.toFloat(), it.bid.toFloat())
                }
                setSuccessStatus()
            }
        }
    }

    private fun setSuccessStatus() {
        _connectionStatus.value = ConnectionStatus.Success
    }

    private fun setLoadingStatus() {
        _connectionStatus.value = ConnectionStatus.Loading
    }

    private fun setErrorStatus() {
        _connectionStatus.value = ConnectionStatus.Error
    }
}