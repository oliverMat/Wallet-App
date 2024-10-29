package com.oliver.wallet

import android.app.Application
import com.oliver.wallet.data.AppContainer
import com.oliver.wallet.data.AppDataContainer

class Application : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}