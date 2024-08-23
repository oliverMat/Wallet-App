package com.oliver.wallet.util

import androidx.annotation.StringRes
import com.oliver.wallet.R

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

enum class WalletScreen(@StringRes val title: Int) {
    Money(title = R.string.nav_name_money),
    Stock(title = R.string.nav_name_stock),
    Calculator(title = R.string.nav_name_calculator),
    MoneyGraphic(title = R.string.nav_name_money_graphic)
}