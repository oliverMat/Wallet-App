package com.oliver.wallet.data

import android.content.Context
import com.oliver.wallet.data.network.MoneyRepository
import com.oliver.wallet.data.network.MoneyRepo
import com.oliver.wallet.data.network.RetrofitInstance
import com.oliver.wallet.data.room.CoinDatabase
import com.oliver.wallet.data.room.repositorio.inter.CoinRepository
import com.oliver.wallet.data.room.repositorio.CoinRepo
import com.oliver.wallet.data.room.repositorio.TaxRepo
import com.oliver.wallet.data.room.repositorio.inter.TaxRepository

interface AppContainer {
    val coinRepository: CoinRepository
    val taxRepository: TaxRepository
    val moneyRepository: MoneyRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val coinRepository: CoinRepository by lazy {
        CoinRepo(CoinDatabase.getDatabase(context).coinDao())
    }

    override val taxRepository: TaxRepository by lazy {
        TaxRepo(CoinDatabase.getDatabase(context).taxDao())
    }

    override val moneyRepository: MoneyRepository by lazy {
        MoneyRepo(RetrofitInstance.api_money)
    }
}