package com.oliver.wallet.ui.view.graphic.money

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
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


@Composable
fun MoneyGraphicView(navController: NavHostController, viewModel: MoneyViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.connectionState) {
        ConnectionStatus.Success -> SuccessScreen(uiState, viewModel, navController)

        ConnectionStatus.Loading -> LoadingScreen(uiState)

        ConnectionStatus.Error -> ErrorScreen()
    }
}

@Composable
private fun SuccessScreen(
    uiState: MoneyUiState,
    viewModel: MoneyViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Row {
                OutlinedCard(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.background)
                ) {
                    IconButton(
                        modifier = Modifier.padding(vertical = 6.dp),
                        onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = MaterialTheme.colorScheme.tertiary,
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
                ElevatedCardTemplate {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(7.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                "Moeda:",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                            Text(
                                "${uiState.coin?.label} - ${uiState.price?.code}",
                                modifier = Modifier.padding(horizontal = 5.dp),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.size(20.dp))
                        MinMaxInList(uiState)
                    }
                }
            }
            DropDown(viewModel, uiState.dailyChart)
        }
        Chart(uiState.chart, Modifier.weight(1f))
    }
}

@Composable
private fun ElevatedCardTemplate(item: @Composable (modifier: Modifier) -> Unit) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        item(Modifier.weight(1f))
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

@Composable
fun MinMaxInList(chart: MoneyUiState) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
        Column {
            Text(
                "Max:", color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                "${chart.getMaxYDecimalChart()} / ${chart.getDateMaxChart()}",
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 5.dp),
            )
        }
        Spacer(modifier = Modifier.size(20.dp))
        Column {
            Text(
                "Min:", color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                "${chart.getMinYDecimalChart()} / ${chart.getDateMinChart()}",
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 5.dp),
            )
        }
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

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedCard(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.background),
            modifier = Modifier
                .clickable {
                    viewModel ?: return@clickable
                    isDropDownExpanded.value = true
                }
        ) {
            Text(
                text = stringResource(list[itemPosition.intValue].first),
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
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
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            "Periodo",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 12.sp,
        )
    }
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

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        modifier = modifier.padding(10.dp)
    ) {
        DescriptionChart()
        AndroidView(
            modifier = modifier
                .padding(10.dp)
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
}

@Composable
private fun DescriptionChart() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
    ) {
        Text(
            stringResource(R.string.money_graphic_description_chart),
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.size(5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .size(14.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .size(3.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                        .align(Alignment.Center) // Centraliza esta Box na Box externa
                )
            }
            Text(
                "Max/Min do periodo",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 10.sp,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        LoadingScreen(MoneyUiState())
    }
}