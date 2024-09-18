package com.oliver.wallet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.oliver.wallet.data.model.MoneyResponse
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.data.network.ResultWrapper
import com.oliver.wallet.data.network.money.MoneyRepository
import com.oliver.wallet.util.ConnectionStatus
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
        setListMoney(symbolMoney)
    }

    fun calculate(value: String) {
        val valueInsert = if (value.isEmpty()) 1f else value.toFloat()

        _uiState.update { moneyUiState ->
            moneyUiState.copy(
                calculate = valueInsert
            )
        }
    }

    private fun loadPeriodically() {
        viewModelScope.launch {
            while (isActive) {
                getCurrentCoinData(_uiState.value.symbol)
                getCoinRatesData(_uiState.value.symbol)
                delay(UPDATE_INTERVAL_30)
            }
        }
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
                setConnectionStatus(ConnectionStatus.Loading)
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

    private suspend fun getCoinRatesData(symbolMoney: TypeMoney) {
        when (val result = moneyRepository.getCoinRatesData(symbolMoney.moneyType)) {
            is ResultWrapper.NetworkError -> {
                setConnectionStatus(ConnectionStatus.Loading)
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