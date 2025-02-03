package com.oliver.wallet.util

import androidx.annotation.StringRes
import com.oliver.wallet.R

enum class TypeMoney(val moneyType: String) {
    Dollar("USD-BRL"),
    Euro("EUR-BRL"),
    BrazilianReal("BRL-USD"),
    JapaneseYen("JPY-BRL"),
    PoundSterling("GBP-BRL"),
    AustralianDollar("AUD-BRL"),
    CanadianDollar("CAD-BRL"),
    SwissFranc("CHF-BRL"),
    ChineseYuan("CNY-BRL"),
    SwedishKrona("SEK-BRL"),
    NewZealandDollar("NZD-BRL"),
    MexicanPeso("MXN-BRL"),
    SingaporeDollar("SGD-BRL"),
    HongKongDollar("HKD-BRL"),
    NorwegianKrone("NOK-BRL"),
    TurkishLira("TRY-BRL"),
    IndianRupee("INR-BRL"),
    RussianRuble("RUB-BRL"),
    SouthAfricanRand("ZAR-BRL"),
    ChileanPeso("CLP-BRL"),
    ArgentinePeso("ARS-BRL"),
    TaiwanDollar("TWD-BRL"),
    ThaiBaht("THB-BRL")

}

enum class ConnectionStatus {
    Success,
    Loading,
    Error
}

enum class WalletScreen(@StringRes val title: Int) {
    Money(title = R.string.nav_name_money),
    Stock(title = R.string.nav_name_stock),
    Calculator(title = R.string.nav_name_calculator),
}