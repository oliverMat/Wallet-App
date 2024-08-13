package com.oliver.wallet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.data.model.MoneyResponse
import com.oliver.wallet.data.model.StockModel
import com.oliver.wallet.data.network.ResultWrapper
import com.oliver.wallet.data.network.money.MoneyRepository
import com.oliver.wallet.data.network.stockexchange.StockRepository
import com.oliver.wallet.util.TypeMoneyEnum
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val moneyRepository = MoneyRepository()
    private val stockRepository = StockRepository()

    private val _currentMoney = MutableStateFlow<MoneyModel?>(null)
    val currentMoney: StateFlow<MoneyModel?> = _currentMoney

    private val _listMoney = MutableStateFlow<List<Entry>?>(null)
    val listMoney: StateFlow<List<Entry>?> = _listMoney

    private val _selectStock = MutableStateFlow<StockModel?>(null)
    val selectStock: StateFlow<StockModel?> = _selectStock

    private val _indexB3Data = MutableStateFlow<StockModel?>(null)
    val indexB3Data: StateFlow<StockModel?> = _indexB3Data

    private val _indexSp500Data = MutableStateFlow<StockModel?>(null)
    val indexSp500Data: StateFlow<StockModel?> = _indexSp500Data

    private var symbolMoney: TypeMoneyEnum = TypeMoneyEnum.Dollar


    init {
        loadMoneyPeriodically()
        loadListMoneyPeriodically()
//        loadB3Index()
//        loadSp500Index()
    }

    private fun loadMoneyPeriodically() {
        viewModelScope.launch {
            while (isActive) {
                getCurrentCoinData(symbolMoney)
                delay(UPDATE_INTERVAL_30)
            }
        }
    }

    private fun loadListMoneyPeriodically() {
        viewModelScope.launch {
            try {
                while (isActive) {
                    getCoinRatesData(symbolMoney.moneyType)
                    delay(UPDATE_INTERVAL_120)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectMoneySymbol(symbolMoney: TypeMoneyEnum) {
        this.symbolMoney = symbolMoney
        setCurrentMoney(symbolMoney)
        setListMoney(symbolMoney)
    }

    private fun setCurrentMoney(symbolMoney: TypeMoneyEnum) {
        viewModelScope.launch {
            getCurrentCoinData(symbolMoney)
        }
    }

    private fun setListMoney(symbolMoney: TypeMoneyEnum) {
        viewModelScope.launch {
            try {
                getCoinRatesData(symbolMoney.moneyType)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private suspend fun getCurrentCoinData(symbolMoney: TypeMoneyEnum) {
        when (val result = moneyRepository.getCurrentCoinData(symbolMoney.moneyType)) {
            is ResultWrapper.NetworkError -> null //showNetworkError()
            is ResultWrapper.GenericError -> null //showGenericError(redditResponse)
            is ResultWrapper.Success -> setResponseMoney(result.value, symbolMoney)
        }
    }

    private fun setResponseMoney(response: MoneyResponse, symbolMoney: TypeMoneyEnum) {
        _currentMoney.value = when (symbolMoney) {
            TypeMoneyEnum.Dollar -> response.dollar
            TypeMoneyEnum.Euro -> response.euro
            TypeMoneyEnum.Bitcoin -> response.bitcoin
        }
    }

    private suspend fun getCoinRatesData(symbolMoney: String) {
        when (val result = moneyRepository.getCoinRatesData(symbolMoney)) {
            is ResultWrapper.NetworkError -> null //showNetworkError()
            is ResultWrapper.GenericError -> null //showGenericError(redditResponse)
            is ResultWrapper.Success -> {
                _listMoney.value = result.value.mapIndexed { index, it ->
                    Entry(index.toFloat(), it.bid.toFloat())
                }
            }
        }
    }


    fun fetchStock(symbol: String) {
        viewModelScope.launch {
            try {
                val response = stockRepository.getStock(symbol)
                _selectStock.value = response.results.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadB3Index() {
        viewModelScope.launch {
            try {
                val response = stockRepository.getIndexB3()
                _indexB3Data.value = response.results.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadSp500Index() {
        viewModelScope.launch {
            try {
                val response = stockRepository.getIndexSp500()
                _indexSp500Data.value = response.results.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val UPDATE_INTERVAL_30 = 30_000L
        private const val UPDATE_INTERVAL_120 = 120_000L
    }
}