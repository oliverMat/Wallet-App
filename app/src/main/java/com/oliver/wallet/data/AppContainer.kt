package com.oliver.wallet.data

import android.content.Context
import com.oliver.wallet.data.network.MoneyRepository
import com.oliver.wallet.data.network.MoneyRepo
import com.oliver.wallet.data.network.RetrofitInstance
import com.oliver.wallet.data.room.CoinDatabase
import com.oliver.wallet.data.room.CoinRepository
import com.oliver.wallet.data.room.CoinRepo

interface AppContainer {
    val coinRepository: CoinRepository
    val moneyRepository: MoneyRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val coinRepository: CoinRepository by lazy {
        CoinRepo(CoinDatabase.getDatabase(context).coinDao())
    }

    override val moneyRepository: MoneyRepository by lazy {
        MoneyRepo(RetrofitInstance.api_money)
    }
}