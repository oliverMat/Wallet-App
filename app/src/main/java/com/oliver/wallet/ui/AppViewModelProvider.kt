package com.oliver.wallet.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.oliver.wallet.Application
import com.oliver.wallet.ui.viewmodel.CoinViewModel
import com.oliver.wallet.ui.viewmodel.MoneyViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            MoneyViewModel(application().container.moneyRepository)
        }

        initializer {
            CoinViewModel(application().container.coinRepository)
        }
    }
}

fun CreationExtras.application(): Application =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as Application)