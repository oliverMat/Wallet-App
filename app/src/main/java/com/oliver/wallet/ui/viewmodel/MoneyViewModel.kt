package com.oliver.wallet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.oliver.wallet.data.model.CalculatorModel
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.data.network.MoneyRepository
import com.oliver.wallet.data.network.ResultWrapper
import com.oliver.wallet.data.room.model.CoinModel
import com.oliver.wallet.data.room.model.TaxModel
import com.oliver.wallet.data.room.repositorio.inter.CoinRepository
import com.oliver.wallet.data.room.repositorio.inter.TaxRepository
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.Constants.DAILY_STANDARD
import com.oliver.wallet.util.Constants.UPDATE_INTERVAL_2_SEG
import com.oliver.wallet.util.Constants.UPDATE_INTERVAL_30_SEG
import com.oliver.wallet.util.TypeMoney
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MoneyViewModel(
    private val moneyRepository: MoneyRepository,
    private val coinRepository: CoinRepository,
    private val taxRepository: TaxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoneyUiState())
    val uiState: StateFlow<MoneyUiState> = _uiState.asStateFlow()

    init {
        loadFavorite()
        loadListOfCoins()
        loadTax()
    }


    fun setFavoriteCoin() {
        viewModelScope.launch {
            val coinFavorite = coinRepository.getFavoriteCoin()
            coinFavorite.isFavorite = false
            coinRepository.update(coinFavorite)

            val currentCoin = _uiState.value.coin
            currentCoin!!.isFavorite = true

            coinRepository.update(currentCoin)
        }
    }

    fun selectCoin(coinModel: CoinModel) {
        _uiState.update { moneyUiState ->
            moneyUiState.copy(
                typeMoney = coinModel.typeMoney,
                coin = coinModel
            )
        }
        setConnectionStatus(ConnectionStatus.Loading)
        viewModelScope.launch {
            delay(UPDATE_INTERVAL_2_SEG)
            setComponents(coinModel.typeMoney)
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
            delay(UPDATE_INTERVAL_2_SEG)
            setComponents(_uiState.value.typeMoney, daily)
        }
    }

    fun calculate(value: String) {
        val newValue = value.replace(",", ".").toFloat()

        _uiState.update { moneyUiState ->
            moneyUiState.copy(
                calculate = CalculatorModel(
                    value = newValue,
                    iof = _uiState.value.calculate.iof,
                    taxa = _uiState.value.calculate.taxa
                )
            )
        }
    }

    fun enableTax(isEnable: Boolean) {
        viewModelScope.launch {
            taxRepository.getAllTaxStream().collect {
                _uiState.update { moneyUiState ->
                    moneyUiState.copy(
                        calculate = when (isEnable) {
                            true -> CalculatorModel(
                                value = _uiState.value.calculate.value,
                                iof = it.iof,
                                taxa = it.taxa
                            )

                            false -> CalculatorModel(
                                value = _uiState.value.calculate.value,
                                iof = 0f,
                                taxa = 0f
                            )
                        }
                    )
                }
            }
        }
    }

    fun updateCalculatorModel(iof: Float?, taxa: Float?) {
        _uiState.update { moneyUiState ->
            moneyUiState.copy(
                calculate = CalculatorModel(
                    value = _uiState.value.calculate.value,
                    iof = iof ?: _uiState.value.calculate.iof,
                    taxa = taxa ?: _uiState.value.calculate.taxa
                )
            )
        }
    }

    fun saveTaxaAndIof() {
        viewModelScope.launch {
            taxRepository.update(
                TaxModel(
                    id = 1,
                    iof = _uiState.value.calculate.iof,
                    taxa = _uiState.value.calculate.taxa
                )
            )
        }
    }

    fun loadTax() {
        viewModelScope.launch {
            taxRepository.getAllTaxStream().collect {
                _uiState.update { moneyUiState ->
                    moneyUiState.copy(
                        calculate = CalculatorModel(
                            iof = it.iof,
                            taxa = it.taxa
                        )
                    )
                }
            }
        }
    }

    private fun loadFavorite() {
        viewModelScope.launch {
            if (_uiState.value.coin == null) {
                _uiState.update { moneyUiState ->
                    val coin = coinRepository.getFavoriteCoin()
                    moneyUiState.copy(
                        coin = coin,
                        typeMoney = coin.typeMoney
                    )
                }
            }
        }.invokeOnCompletion {
            loadPeriodically()
        }
    }

    private fun loadListOfCoins() {
        viewModelScope.launch {
            coinRepository.getAllCoinStream().collect {
                _uiState.update { moneyUiState ->
                    moneyUiState.copy(
                        listCoin = it
                    )
                }
            }
        }
    }

    private fun loadPeriodically() {
        viewModelScope.launch {
            while (isActive) {
                setComponents(_uiState.value.typeMoney, _uiState.value.dailyChart)
                delay(UPDATE_INTERVAL_30_SEG)
            }
        }
    }

    private suspend fun setComponents(symbolMoney: TypeMoney, daily: String = DAILY_STANDARD) {
        getPriceOfDay(symbolMoney)
        getChartForPeriod(symbolMoney, daily)
    }

    private suspend fun getPriceOfDay(symbolMoney: TypeMoney) {
        when (val result = moneyRepository.getPriceOfDay(symbolMoney.moneyType)) {
            is ResultWrapper.NetworkError -> {
                setConnectionStatus(ConnectionStatus.Error)
            }

            is ResultWrapper.GenericError -> {
                println(result.code)
                setConnectionStatus(ConnectionStatus.Error)
            }

            is ResultWrapper.Success -> {
                _uiState.update { moneyUiState ->
                    moneyUiState.copy(
                        price = when (symbolMoney) {
                            TypeMoney.Dollar -> result.value.dollar
                            TypeMoney.Euro -> result.value.euro
                        }
                    )
                }
                setConnectionStatus(ConnectionStatus.Success)
            }
        }
    }

    private suspend fun getChartForPeriod(symbolMoney: TypeMoney, daily: String) {
        when (val result = moneyRepository.getChartForPeriod(symbolMoney.moneyType, daily)) {
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