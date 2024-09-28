package com.oliver.wallet.ui.view.graphic.money

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.DateValueFormatter


@Composable
fun MoneyGraphicView(viewModel: MoneyViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        when (uiState.connectionState) {
            ConnectionStatus.Success -> {
                DropDownDemo(viewModel)
                Chart(uiState.chart)
            }

            ConnectionStatus.Loading -> {

            }

            ConnectionStatus.Error -> {

            }
        }
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
            setDrawValues(false)
            setDrawCircles(false)
            fillColor = primaryColor
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            valueTextSize = 14f
            valueTextColor = secondaryColor
        }
        lineData = LineData(dataSet)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(40.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .height(200.dp),
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
                        isGranularityEnabled = false
                    }

                    axisLeft.apply {
                        setDrawGridLines(false) // Disable grid lines
                        setDrawAxisLine(false) // Disable axis line
                        textSize = 14f // Set the font size for Y axis labels
                        textColor = secondaryColor

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
fun DropDownDemo(viewModel: MoneyViewModel) {

    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    val itemPosition = remember {
        mutableIntStateOf(0)
    }

    val list = listOf(
        "5 Dias" to "5",
        "15 Dias" to "15",
        "1 Mês" to "30",
        "2 Mesês" to "60",
        "3 Mesês" to "90",
        "6 Mesês" to "180",
        "1 Ano" to "365"
    )

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
        isDropDownExpanded.value = true
    }) {
        Text(text = "Periodo", color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.size(10.dp))
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(30.dp)
                .border(
                    width = 2.dp, // Largura da borda
                    color = MaterialTheme.colorScheme.secondary, // Cor da borda
                    shape = RoundedCornerShape(20.dp) // Forma arredondada
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = list[itemPosition.intValue].first,
                    color = MaterialTheme.colorScheme.secondary
                )
                viewModel.setPeriodChart(list[itemPosition.intValue].second)
            }
            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = {
                    isDropDownExpanded.value = false
                }) {
                list.forEachIndexed { index, username ->
                    DropdownMenuItem(text = {
                        Text(text = username.first)
                    },
                        onClick = {
                            isDropDownExpanded.value = false
                            itemPosition.intValue = index
                        })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        MoneyGraphicView(MoneyViewModel())
    }
}