package com.oliver.wallet.data

import android.content.Context
import com.oliver.wallet.data.network.MoneyRepository
import com.oliver.wallet.data.network.MoneyRepo
import com.oliver.wallet.data.network.RetrofitInstance
import com.oliver.wallet.data.room.RomDatabases
import com.oliver.wallet.data.room.repositorio.inter.CoinRepository
import com.oliver.wallet.data.room.repositorio.CoinRepo
import com.oliver.wallet.data.room.repositorio.RatesRepo
import com.oliver.wallet.data.room.repositorio.inter.RatesRepository

interface AppContainer {
    val coinRepository: CoinRepository
    val ratesRepository: RatesRepository
    val moneyRepository: MoneyRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val coinRepository: CoinRepository by lazy {
        CoinRepo(RomDatabases.getDatabase(context).coinDao())
    }

    override val ratesRepository: RatesRepository by lazy {
        RatesRepo(RomDatabases.getDatabase(context).ratesDao())
    }

    override val moneyRepository: MoneyRepository by lazy {
        MoneyRepo(RetrofitInstance.api_money)
    }
}