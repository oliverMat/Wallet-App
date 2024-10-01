package com.oliver.wallet.ui.view.money

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.oliver.wallet.R
import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.common.ComposableLifecycle
import com.oliver.wallet.ui.view.common.ShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.DateValueFormatter
import com.oliver.wallet.util.TypeMoney
import com.oliver.wallet.util.WalletScreen
import com.oliver.wallet.util.dataFormat
import com.oliver.wallet.util.toDecimalFormat

@Composable
fun MoneyView(navController: NavHostController, viewModel: MoneyViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LifeCycle(viewModel)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary)
            .verticalScroll(rememberScrollState())
    ) {
        when (uiState.connectionState) {
            ConnectionStatus.Success -> {
                Spacer(modifier = Modifier.size(10.dp))
                Price(uiState.price)
                Spacer(modifier = Modifier.size(40.dp))
                MaxMin(uiState.price)
                Spacer(modifier = Modifier.size(40.dp))
                Chart(uiState.chart)
                Spacer(modifier = Modifier.size(10.dp))
                Date(uiState.price)
                Spacer(modifier = Modifier.size(10.dp))
            }

            ConnectionStatus.Loading -> {
                EffectShimmerEffect()
            }

            ConnectionStatus.Error -> {

            }
        }
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(15.dp))
                SingleSelectChipList(viewModel, uiState)
                Spacer(modifier = Modifier.size(20.dp))
                Row {
                    ButtonBox(
                        text = "Calculadora",
                        icon = R.drawable.calculate,
                        Modifier.weight(1f),
                        onClick = { navController.navigate(WalletScreen.Calculator.name) }
                    )
                    ButtonBox(
                        text = "Grafico",
                        icon = R.drawable.bar_chart,
                        Modifier.weight(1f),
                        onClick = { navController.navigate(WalletScreen.MoneyGraphic.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LifeCycle(viewModel: MoneyViewModel) {
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                viewModel.setPeriodChart()
            }

            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SingleSelectChipList(viewModel: MoneyViewModel, uiState: MoneyUiState) {
    val label = stringArrayResource(R.array.list_money_label).toList()

    var selected by remember {
        mutableStateOf<String?>(
            label[when (uiState.symbol) {
                TypeMoney.Dollar -> 0
                TypeMoney.Euro -> 1
            }]
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Moedas:")
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            label.forEachIndexed { index, it ->
                val isSelected = it == selected
                Chip(
                    border = BorderStroke(
                        width = 1.0.dp, MaterialTheme.colorScheme.tertiary
                    ),
                    colors = ChipDefaults.chipColors(backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
                    onClick = {
                        selected = if (isSelected) selected else it

                        viewModel.selectMoneySymbol(
                            when (index) {
                                0 -> TypeMoney.Dollar
                                else -> TypeMoney.Euro
                            }
                        )
                    },
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 19.dp),
                        text = it,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
fun Price(price: MoneyModel?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "1 ${price?.code} é igual a:",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(start = 10.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.money_icon_white),
                contentDescription = "Custom Money Icon",
                Modifier.size(26.dp)
            )
            Text(
                text = "${price?.bid?.toFloat()?.toDecimalFormat()}",
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.background
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "-",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.background
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "%: ${price?.pctChange}",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = negativeValueColor("${price?.pctChange}")
            )
        }
    }
}

@Composable
fun MaxMin(price: MoneyModel?) {
    Text(
        text = "Max/Min do dia:",
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.background
    )
    Spacer(modifier = Modifier.size(10.dp))
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.arrow_up),
            contentDescription = "Custom Money Icon",
        )
        Text(
            text = "${price?.high?.toFloat()?.toDecimalFormat()}",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.background
        )
        Spacer(modifier = Modifier.size(5.dp))
        Image(
            painter = painterResource(id = R.drawable.arrow_down),
            contentDescription = "Custom Money Icon",
        )
        Text(
            text = "${price?.low?.toFloat()?.toDecimalFormat()}",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun negativeValueColor(value: String?): Color {
    return if (value?.contains("-") == true) {
        MaterialTheme.colorScheme.onError
    } else {
        MaterialTheme.colorScheme.primary
    }
}

@Composable
fun Chart(listItems: List<Entry>?) {
    var lineData by remember { mutableStateOf(LineData()) }

    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val secondaryColor = MaterialTheme.colorScheme.secondary.toArgb()

    LaunchedEffect(listItems) {
        val dataSet = LineDataSet(listItems, "").apply {
            color = primaryColor
            setCircleColor(primaryColor)
            lineWidth = 2f
            circleRadius = 4f
            setDrawFilled(false)
            fillColor = primaryColor
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            valueTextSize = 14f
            valueTextColor = secondaryColor
        }
        lineData = LineData(dataSet)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        Text(
            text = "Ultimos 5 dias",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .height(100.dp),
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false // Remove the description
                    setTouchEnabled(false)
                    setPinchZoom(false)

                    xAxis.apply {
                        setDrawGridLines(false) // Disable grid lines
                        setDrawAxisLine(true) // Disable axis line
                        setDrawLabels(true) // Disable the X axis labels
                        textColor = secondaryColor
                        position = XAxis.XAxisPosition.BOTTOM
                        textSize = 14f // Set the font size for X axis labels
                        spaceMin = 0.5f
                        valueFormatter = DateValueFormatter()
                        axisMinimum = 0f
                        labelRotationAngle = -45f
                        isGranularityEnabled = true
                        isEnabled = false
                    }

                    axisLeft.apply {
                        setDrawGridLines(false) // Disable grid lines
                        setDrawAxisLine(false) // Disable axis line
                        textSize = 14f // Set the font size for Y axis labels
                        textColor = secondaryColor
                        isEnabled = false
                    }

                    axisRight.isEnabled = false // Disable the right Y axis
                    legend.isEnabled = false // Disable the legend

                    data = lineData
                    invalidate()
                }
            },
            update = {
                it.data = lineData
                it.invalidate()
            })
    }
}

@Composable
fun Date(price: MoneyModel?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Ultima atualização:",
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "${price?.create_date?.dataFormat()}",
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ButtonBox(text: String, icon: Int, modifier: Modifier, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(14.dp)
            )
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.primary)
            .padding(10.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "Custom Money Icon"
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = text)
        }
    }
}

@Composable
fun EffectShimmerEffect() {
    ShimmerEffect(
        modifier = Modifier
            .fillMaxWidth()
            .height(387.5.dp)
            .padding(10.dp)
            .background(
                MaterialTheme.colorScheme.tertiary,
                RoundedCornerShape(10.dp)
            )
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        MoneyView(rememberNavController(), MoneyViewModel())
    }
}