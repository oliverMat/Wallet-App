package com.oliver.wallet.ui.view.graphic.money

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.oliver.wallet.R
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.common.CustomMarkerView
import com.oliver.wallet.ui.view.common.ShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.DateValueFormatter
import com.oliver.wallet.util.TypeMoney


@Composable
fun MoneyGraphicView(viewModel: MoneyViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.connectionState) {
        ConnectionStatus.Success -> SuccessScreen(uiState, viewModel)

        ConnectionStatus.Loading -> LoadingScreen(uiState)

        ConnectionStatus.Error -> ErrorScreen()
    }
}

@Composable
private fun SuccessScreen(uiState: MoneyUiState, viewModel: MoneyViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.tertiary)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SingleSelectChipList(viewModel, uiState, Modifier.weight(1f))
            Spacer(modifier = Modifier.size(10.dp))
            MinMaxInList(uiState)
            Spacer(modifier = Modifier.size(20.dp))
            DropDown(viewModel, uiState.dailyChart)
        }
        Spacer(modifier = Modifier.size(20.dp))
        DescriptionChart()
        Chart(uiState.chart, Modifier.weight(1f))
        Spacer(modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun LoadingScreen(uiState: MoneyUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.tertiary)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SingleSelectChipList(null, uiState, Modifier.weight(1f))
            Spacer(modifier = Modifier.size(10.dp))
            ShimmerEffect(
                modifier = Modifier
                    .width(205.dp)
                    .height(40.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(10.dp)
                    )
            )
            Spacer(modifier = Modifier.size(20.dp))
            DropDown(null, uiState.dailyChart)
        }
        Spacer(modifier = Modifier.size(20.dp))
        DescriptionChart()
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp)
                .background(
                    MaterialTheme.colorScheme.tertiary,
                    RoundedCornerShape(10.dp)
                )
        )
    }
}

@Composable
private fun ErrorScreen() {

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SingleSelectChipList(
    viewModel: MoneyViewModel?,
    uiState: MoneyUiState,
    modifier: Modifier
) {
    val label = stringArrayResource(R.array.list_money_label).toList()

    val selected by remember {
        mutableStateOf<String?>(
            label[when (uiState.symbol) {
                TypeMoney.Dollar -> 0
                TypeMoney.Euro -> 1
            }]
        )
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            label.forEachIndexed { index, it ->
                val isSelected = it == selected
                Chip(
                    border = BorderStroke(
                        width = 1.0.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    ),
                    colors = ChipDefaults.chipColors(backgroundColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
                    onClick = {
                        viewModel ?: return@Chip

                        if (selected == it) return@Chip

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
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun MinMaxInList(chart: MoneyUiState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(5.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(2.dp)
            ) {}
        }
        Spacer(modifier = Modifier.size(12.dp))
        Text("Max:", color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            "${chart.getMaxYDecimalChart()}\n${chart.getDateMaxChart()}",
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text("Min:", color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            "${chart.getMinYDecimalChart()}\n${chart.getDateMinChart()}",
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DropDown(viewModel: MoneyViewModel?, dailyChart: String) {

    val list = listOf(
        R.string.money_graphic_5_days to 5,
        R.string.money_graphic_15_days to 15,
        R.string.money_graphic_1_months to 30,
        R.string.money_graphic_2_months to 60,
        R.string.money_graphic_3_months to 90,
        R.string.money_graphic_6_months to 180,
        R.string.money_graphic_1_year to 365
    )

    val initialIndex = list.indexOfFirst { it.second == dailyChart.toInt() }.coerceAtLeast(0)
    val itemPosition = remember { mutableIntStateOf(initialIndex) }

    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
        viewModel ?: return@clickable
        isDropDownExpanded.value = true
    }) {
        Box(
            modifier = Modifier
                .width(75.dp)
                .height(35.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(list[itemPosition.intValue].first),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = {
                    isDropDownExpanded.value = false
                }) {
                list.forEachIndexed { index, period ->
                    DropdownMenuItem(text = {
                        Text(text = stringResource(period.first))
                    },
                        onClick = {
                            viewModel ?: return@DropdownMenuItem
                            isDropDownExpanded.value = false
                            itemPosition.intValue = index
                            viewModel.setPeriodChart(period.second.toString())
                        })
                }
            }
        }
    }
}

@Composable
private fun DescriptionChart() {
    Text(
        stringResource(R.string.money_graphic_description_chart),
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun Chart(listItems: List<Entry>?, modifier: Modifier) {
    var lineData by remember { mutableStateOf(LineData()) }

    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val secondaryColor = MaterialTheme.colorScheme.secondary.toArgb()

    val customMarkerView = CustomMarkerView(LocalContext.current)

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
            setDrawHighlightIndicators(false)
        }

        val minEntry = listItems?.minByOrNull { it.y }
        val maxEntry = listItems?.maxByOrNull { it.y }

        val minMaxEntries = listOfNotNull(minEntry, maxEntry)
        val minMaxDataSet = LineDataSet(minMaxEntries, "").apply {
            color = secondaryColor
            setCircleColor(primaryColor)
            circleRadius = 7f
            setDrawValues(false)
            setDrawHighlightIndicators(false)
            valueTextSize = 14f
            valueTextColor = secondaryColor
        }

        lineData = LineData(dataSet, minMaxDataSet)
    }

    AndroidView(
        modifier = modifier
            .fillMaxSize(),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false // Remove the description
                setTouchEnabled(true)
                setPinchZoom(false)
                setScaleEnabled(false)

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
                    labelRotationAngle = 0f
                    isGranularityEnabled = true
                }

                axisLeft.apply {
                    setDrawGridLines(false) // Disable grid lines
                    setDrawAxisLine(false) // Disable axis line
                    textSize = 14f // Set the font size for Y axis labels
                    textColor = secondaryColor
                }

                customMarkerView.setMarkerView(listItems)
                marker = customMarkerView

                axisRight.isEnabled = false // Disable the right Y axis
                legend.isEnabled = false // Disable the legend

                data = lineData
                invalidate()
            }
        },
        update = {
            it.data = lineData
            customMarkerView.setMarkerView(listItems)
            it.marker = customMarkerView
            it.invalidate()
        })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        MoneyGraphicView(MoneyViewModel())
    }
}