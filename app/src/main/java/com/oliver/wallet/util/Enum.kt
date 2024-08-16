package com.oliver.wallet.util

enum class TypeMoney(val moneyType: String) {
    Dollar("USD-BRL"),
    Euro("EUR-BRL"),
    Bitcoin("BTC-BRL")
}

enum class ConnectionStatus() {
    Success,
    Loading,
    Error
}