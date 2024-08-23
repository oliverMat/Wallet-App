package com.oliver.wallet.ui.view.money

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oliver.wallet.ui.view.money.content.GraphicShimmerEffect
import com.oliver.wallet.ui.view.money.content.MenuTop
import com.oliver.wallet.ui.view.money.content.MoneyChart
import com.oliver.wallet.ui.view.money.content.PriceBox
import com.oliver.wallet.ui.view.money.content.PriceShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus

class MoneyView {

    @Composable
    fun View(viewModel: MoneyViewModel = viewModel()) {
        val status by viewModel.connectionStatus.collectAsState()
        val price by viewModel.price.collectAsState()
        val moneyChart by viewModel.moneyChart.collectAsState()

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            MenuTop(viewModel)
            Column {
                when (status) {
                    ConnectionStatus.Success -> {
                        PriceBox(price)
                        MoneyChart(moneyChart)
                    }

                    ConnectionStatus.Loading -> {
                        PriceShimmerEffect()
                        GraphicShimmerEffect()
                    }

                    ConnectionStatus.Error -> {

                    }
                }
            }
        }
    }
}