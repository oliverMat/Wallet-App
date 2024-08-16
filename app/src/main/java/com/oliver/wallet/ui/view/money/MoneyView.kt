package com.oliver.wallet.ui.view.money

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oliver.wallet.R
import com.oliver.wallet.ui.view.ShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.dataFormat
import com.oliver.wallet.util.toDecimalFormat

class MoneyView {

    @Composable
    fun View() {
        Scaffold(backgroundColor = MaterialTheme.colorScheme.background, topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colorScheme.secondary,
                title = {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.wallet_icon),
                            contentDescription = "Custom Money Icon",
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Text("Wallet", color = MaterialTheme.colorScheme.tertiary)
                    }
                },
                elevation = 8.dp
            )
        }, content = { padding ->
            Content(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        })
    }

    @Composable
    private fun Content(viewModel: MoneyViewModel = viewModel(), modifier: Modifier) {
        val status by viewModel.connectionStatus.collectAsState()
        val price by viewModel.price.collectAsState()
        val moneyChart by viewModel.moneyChart.collectAsState()

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            SingleSelectChipList(viewModel)
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 5.dp, horizontal = 10.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(10.dp)
            ) {
                Column {
                    when (status) {
                        ConnectionStatus.Success -> {
                            Price(
                                image = R.drawable.show_chart_icon,
                                title = "${price?.name}",
                                textTop = "R$: ${price?.bid?.toFloat()?.toDecimalFormat()}",
                                textMiddle = "%: ${price?.pctChange}",
                                textBottom = "${price?.create_date?.dataFormat()}"
                            )
                            MoneyChart(moneyChart)
                        }

                        ConnectionStatus.Loading -> {
                            PriceShimmerEffect(
                                image = R.drawable.show_chart_icon
                            )
                            ShimmerEffect(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(370.dp)
                                    .padding(
                                        start = 10.dp,
                                        end = 10.dp,
                                        top = 20.dp,
                                        bottom = 10.dp
                                    )
                                    .background(
                                        MaterialTheme.colorScheme.tertiary,
                                        RoundedCornerShape(10.dp)
                                    )
                            )
                        }

                        ConnectionStatus.Error -> {

                        }
                    }
                }
            }
        }
    }
}