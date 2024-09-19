package com.oliver.wallet.data.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MoneyModelTest {

    private val gson = Gson()

    @Test
    fun deserializationMoneyModel() {
        val json = """
        {
            "USDBRL": {
                "code": "USD",
                "codein": "BRL",
                "name": "Dólar Americano/Real Brasileiro",
                "high": "5.4200",
                "low": "5.3700",
                "varBid": "-0.0100",
                "pctChange": "-0.18",
                "bid": "5.4000",
                "ask": "5.4200",
                "timestamp": "1633024800",
                "create_date": "2024-09-17 10:00:00"
            },
            "EURBRL": {
                "code": "EUR",
                "codein": "BRL",
                "name": "Euro/Real Brasileiro",
                "high": "6.4200",
                "low": "6.3700",
                "varBid": "-0.0100",
                "pctChange": "-0.16",
                "bid": "6.4000",
                "ask": "6.4200",
                "timestamp": "1633024800",
                "create_date": "2024-09-17 10:00:00"
            }
        }
        """.trimIndent()

        val moneyResponse = gson.fromJson(json, MoneyResponse::class.java)

        // Verificando o objeto Dollar
        assertNotNull(moneyResponse.dollar)
        assertEquals("USD", moneyResponse.dollar.code)
        assertEquals("BRL", moneyResponse.dollar.codein)
        assertEquals("Dólar Americano/Real Brasileiro", moneyResponse.dollar.name)
        assertEquals("5.4200", moneyResponse.dollar.high)
        assertEquals("5.3700", moneyResponse.dollar.low)
        assertEquals("-0.0100", moneyResponse.dollar.varBid)
        assertEquals("-0.18", moneyResponse.dollar.pctChange)
        assertEquals("5.4000", moneyResponse.dollar.bid)
        assertEquals("5.4200", moneyResponse.dollar.ask)
        assertEquals("1633024800", moneyResponse.dollar.timestamp)
        assertEquals("2024-09-17 10:00:00", moneyResponse.dollar.create_date)

        // Verificando o objeto Euro
        assertNotNull(moneyResponse.euro)
        assertEquals("EUR", moneyResponse.euro.code)
        assertEquals("BRL", moneyResponse.euro.codein)
        assertEquals("Euro/Real Brasileiro", moneyResponse.euro.name)
        assertEquals("6.4200", moneyResponse.euro.high)
        assertEquals("6.3700", moneyResponse.euro.low)
        assertEquals("-0.0100", moneyResponse.euro.varBid)
        assertEquals("-0.16", moneyResponse.euro.pctChange)
        assertEquals("6.4000", moneyResponse.euro.bid)
        assertEquals("6.4200", moneyResponse.euro.ask)
        assertEquals("1633024800", moneyResponse.euro.timestamp)
        assertEquals("2024-09-17 10:00:00", moneyResponse.euro.create_date)
    }
}