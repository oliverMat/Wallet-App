package com.oliver.wallet.ui.view.money

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.money.content.BottomSheetMoreOptions
import com.oliver.wallet.ui.view.money.content.GraphicShimmerEffect
import com.oliver.wallet.ui.view.money.content.GridButtons
import com.oliver.wallet.ui.view.money.content.MoneyChart
import com.oliver.wallet.ui.view.money.content.PriceBox
import com.oliver.wallet.ui.view.money.content.PriceShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus

@Composable
fun MoneyView(navController: NavHostController, viewModel: MoneyViewModel = viewModel()) {
    val status by viewModel.connectionStatus.collectAsState()
    val price by viewModel.price.collectAsState()
    val moneyChart by viewModel.moneyChart.collectAsState()

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            GridButtons(viewModel)
            BottomSheetMoreOptions(navController)
        }
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        MoneyView(rememberNavController())
    }
}