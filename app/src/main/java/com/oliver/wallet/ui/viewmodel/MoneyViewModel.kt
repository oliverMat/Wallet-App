package com.oliver.wallet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.oliver.wallet.data.model.MoneyResponse
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.data.network.ResultWrapper
import com.oliver.wallet.data.network.money.MoneyRepository
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.Constants.DAILY_STANDARD
import com.oliver.wallet.util.Constants.UPDATE_INTERVAL_1
import com.oliver.wallet.util.Constants.UPDATE_INTERVAL_30
import com.oliver.wallet.util.TypeMoney
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MoneyViewModel : ViewModel() {
    private val moneyRepository = MoneyRepository()

    private val _uiState = MutableStateFlow(MoneyUiState())
    val uiState: StateFlow<MoneyUiState> = _uiState.asStateFlow()


    init {
        loadPeriodically()
    }

    fun selectMoneySymbol(symbolMoney: TypeMoney) {
        _uiState.update { moneyUiState ->
            moneyUiState.copy(
                symbol = symbolMoney
            )
        }
        setConnectionStatus(ConnectionStatus.Loading)
        setCurrentMoney(symbolMoney)
        setChart(symbolMoney)
    }

    fun calculate(value: String) {
        val valueInsert = if (value.isEmpty()) 1f else value.toFloat()

        _uiState.update { moneyUiState ->
            moneyUiState.copy(
                calculate = valueInsert
            )
        }
    }

    fun setPeriodChart(daily: String = DAILY_STANDARD) {
        _uiState.update { moneyUiState ->
            moneyUiState.copy(
                dailyChart = daily
            )
        }
        setConnectionStatus(ConnectionStatus.Loading)
        viewModelScope.launch {
            getCoinDaily(_uiState.value.symbol, daily)
        }
    }

    private fun loadPeriodically() {
        viewModelScope.launch {
            while (isActive) {
                getCurrentCoinData(_uiState.value.symbol)
                delay(UPDATE_INTERVAL_30)
            }
        }
    }

    private fun setCurrentMoney(symbolMoney: TypeMoney) {
        viewModelScope.launch {
            getCurrentCoinData(symbolMoney)
        }
    }

    private fun setChart(symbolMoney: TypeMoney) {
        viewModelScope.launch {
            getCoinDaily(symbolMoney, _uiState.value.dailyChart)
        }
    }

    private suspend fun getCurrentCoinData(symbolMoney: TypeMoney) {
        delay(UPDATE_INTERVAL_1)
        when (val result = moneyRepository.getCurrentCoinData(symbolMoney.moneyType)) {
            is ResultWrapper.NetworkError -> {
                setConnectionStatus(ConnectionStatus.Error)
            }

            is ResultWrapper.GenericError -> {
                println(result.code)
                setConnectionStatus(ConnectionStatus.Error)
            }

            is ResultWrapper.Success -> {
                setResponseMoney(result.value, symbolMoney)
                setConnectionStatus(ConnectionStatus.Success)
            }
        }
    }

    private fun setResponseMoney(response: MoneyResponse, symbolMoney: TypeMoney) {
        _uiState.update { moneyUiState ->
            moneyUiState.copy(
                price = when (symbolMoney) {
                    TypeMoney.Dollar -> response.dollar
                    TypeMoney.Euro -> response.euro
                }
            )
        }
    }

    private suspend fun getCoinDaily(symbolMoney: TypeMoney, daily: String = DAILY_STANDARD) {
        delay(UPDATE_INTERVAL_1)
        when (val result = moneyRepository.getCoinDaily(symbolMoney.moneyType, daily)) {
            is ResultWrapper.NetworkError -> {
                setConnectionStatus(ConnectionStatus.Error)
            }

            is ResultWrapper.GenericError -> {
                println(result.code)
                setConnectionStatus(ConnectionStatus.Error)
            }

            is ResultWrapper.Success -> {
                _uiState.update { moneyUiState ->
                    moneyUiState.copy(chart = result.value.mapIndexed { index, it ->
                        Entry(index.toFloat(), it.bid!!.toFloat())
                    })
                }
                setConnectionStatus(ConnectionStatus.Success)
            }
        }
    }

    private fun setConnectionStatus(connectionState: ConnectionStatus) {
        _uiState.update { moneyUiState ->
            moneyUiState.copy(
                connectionState = connectionState
            )
        }
    }
}