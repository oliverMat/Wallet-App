package com.oliver.wallet.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.oliver.wallet.Application
import com.oliver.wallet.ui.viewmodel.MoneyViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            MoneyViewModel(
                application().container.moneyRepository,
                application().container.coinRepository,
                application().container.ratesRepository,
            )
        }
    }
}

fun CreationExtras.application(): Application =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as Application)