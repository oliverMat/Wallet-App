package com.oliver.wallet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.oliver.wallet.data.model.CalculatorModel
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.data.network.MoneyRepository
import com.oliver.wallet.data.network.ResultWrapper
import com.oliver.wallet.data.room.model.CoinModel
import com.oliver.wallet.data.room.model.RatesModel
import com.oliver.wallet.data.room.repositorio.inter.CoinRepository
import com.oliver.wallet.data.room.repositorio.inter.RatesRepository
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
    private val ratesRepository: RatesRepository
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
                    spread = _uiState.value.calculate.spread
                )
            )
        }
    }

    fun enableTax(isEnable: Boolean) {
        viewModelScope.launch {
            ratesRepository.getAllRatesStream().collect {
                _uiState.update { moneyUiState ->
                    moneyUiState.copy(
                        calculate = when (isEnable) {
                            true -> CalculatorModel(
                                value = _uiState.value.calculate.value,
                                iof = it.iof,
                                spread = it.spread
                            )

                            false -> CalculatorModel(
                                value = _uiState.value.calculate.value,
                                iof = 0f,
                                spread = 0f
                            )
                        }
                    )
                }
            }
        }
    }

    fun updateCalculatorModel(iof: Float?, spread: Float?) {
        _uiState.update { moneyUiState ->
            moneyUiState.copy(
                calculate = CalculatorModel(
                    value = _uiState.value.calculate.value,
                    iof = iof ?: _uiState.value.calculate.iof,
                    spread = spread ?: _uiState.value.calculate.spread
                )
            )
        }
    }

    fun saveSpreadAndIof() {
        viewModelScope.launch {
            ratesRepository.update(
                RatesModel(
                    id = 1,
                    iof = _uiState.value.calculate.iof,
                    spread = _uiState.value.calculate.spread
                )
            )
        }
    }

    fun loadTax() {
        viewModelScope.launch {
            ratesRepository.getAllRatesStream().collect {
                _uiState.update { moneyUiState ->
                    moneyUiState.copy(
                        calculate = CalculatorModel(
                            value = _uiState.value.calculate.value,
                            iof = it.iof,
                            spread = it.spread
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
                            TypeMoney.BrazilianReal -> result.value.brazilianReal
                            TypeMoney.JapaneseYen -> result.value.japaneseYen
                            TypeMoney.PoundSterling -> result.value.poundSterling
                            TypeMoney.AustralianDollar -> result.value.australianDollar
                            TypeMoney.CanadianDollar -> result.value.canadianDollar
                            TypeMoney.SwissFranc -> result.value.swissFranc
                            TypeMoney.ChineseYuan -> result.value.chineseYuan
                            TypeMoney.SwedishKrona -> result.value.swedishKrona
                            TypeMoney.NewZealandDollar -> result.value.newZealandDollar
                            TypeMoney.MexicanPeso -> result.value.mexicanPeso
                            TypeMoney.SingaporeDollar -> result.value.singaporeDollar
                            TypeMoney.HongKongDollar -> result.value.hongKongDollar
                            TypeMoney.NorwegianKrone -> result.value.norwegianKrone
                            TypeMoney.TurkishLira -> result.value.turkishLira
                            TypeMoney.IndianRupee -> result.value.indianRupee
                            TypeMoney.RussianRuble -> result.value.russianRuble
                            TypeMoney.SouthAfricanRand -> result.value.southAfricanRand
                            TypeMoney.ChileanPeso -> result.value.chileanPeso
                            TypeMoney.ArgentinePeso -> result.value.argentinePeso
                            TypeMoney.TaiwanDollar -> result.value.taiwanDollar
                            TypeMoney.ThaiBaht -> result.value.thaiBaht
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