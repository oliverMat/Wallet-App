package com.oliver.wallet.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.oliver.wallet.data.room.CoinRepository

class CoinViewModel(private val coinRepository: CoinRepository) : ViewModel() {
}