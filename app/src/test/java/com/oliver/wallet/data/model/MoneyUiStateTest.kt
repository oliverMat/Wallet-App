package com.oliver.wallet.data.model

import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.Constants.DAILY_STANDARD
import com.oliver.wallet.util.TypeMoney
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.reflect.full.memberProperties

class MoneyUiStateTest {

    @Test
    fun testCountFieldsInModel() {
        val fieldCount = MoneyUiState::class.memberProperties.size

        assertEquals(6, fieldCount)
    }

    @Test
    fun testDefaultMoneyUiStateValues() {
        val uiState = MoneyUiState()

        assertEquals(ConnectionStatus.Loading, uiState.connectionState)

        assertEquals(TypeMoney.Dollar, uiState.symbol)

        assertNull(uiState.price)

        assertNull(uiState.chart)

        assertEquals(1f, uiState.calculate, 0.0f)

        assertEquals(uiState.dailyChart, DAILY_STANDARD)
    }

    @Test
    fun testGetCalculateResultWhenPriceIsNull() {
        val uiState = MoneyUiState(
            calculate = 2f,
            price = null
        )

        val result = uiState.getCalculateResult()
        assertEquals(2f, result, 0.0f)
    }

    @Test
    fun testGetCalculateResultWhenBidIsNull() {
        val moneyModel = MoneyModel(
            code = "USD",
            codein = "BRL",
            name = "Dollar/Real",
            high = "5.42",
            low = "5.35",
            varBid = "0.02",
            pctChange = "0.1",
            bid = null,
            ask = "5.45",
            timestamp = "1633024800",
            create_date = "2024-09-17 10:00:00"
        )

        val uiState = MoneyUiState(
            calculate = 3f,
            price = moneyModel
        )

        val result = uiState.getCalculateResult()
        assertEquals(3f, result, 0.0f)
    }

    @Test
    fun testGetCalculateResultWhenBidHasValue() {
        val moneyModel = MoneyModel(
            code = "USD",
            codein = "BRL",
            name = "Dollar/Real",
            high = "5.42",
            low = "5.35",
            varBid = "0.02",
            pctChange = "0.1",
            bid = "5.40",
            ask = "5.45",
            timestamp = "1633024800",
            create_date = "2024-09-17 10:00:00"
        )

        val uiState = MoneyUiState(
            calculate = 2f,
            price = moneyModel
        )

        val result = uiState.getCalculateResult()
        assertEquals(10.8f, result, 0.0f)
    }
}