package com.oliver.wallet.ui.view.money

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.oliver.wallet.R
import com.oliver.wallet.data.network.MoneyModel
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.data.room.model.CoinModel
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.common.ErrorScreenTemplate
import com.oliver.wallet.ui.view.common.ShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.DateValueFormatter
import com.oliver.wallet.util.TypeMoney
import com.oliver.wallet.util.WalletScreen
import com.oliver.wallet.util.toDecimalFormatTreePlaces
import kotlinx.coroutines.launch

@Composable
fun MoneyView(
    navController: NavHostController,
    viewModel: MoneyViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 5.dp)
    ) {
        when (uiState.connectionState) {
            ConnectionStatus.Success -> SuccessScreen(uiState, navController, viewModel)

            ConnectionStatus.Loading -> LoadingScreen()

            ConnectionStatus.Error -> ErrorScreen(uiState, viewModel)
        }
    }
}

@Composable
private fun SuccessScreen(
    uiState: MoneyUiState,
    navController: NavHostController,
    viewModel: MoneyViewModel
) {
    TitleText(stringResource(R.string.money_home_current_quote))
    Price(uiState.price)
    TitleText(stringResource(R.string.money_home_variation_of_day))
    MaxMin(uiState.price)
    TitleText(stringResource(R.string.money_home_coin))
    NameMoney(uiState)
    Chart(uiState.chart, uiState.dailyChart)
    SingleSelectableChips(viewModel, uiState.dailyChart)
    Spacer(modifier = Modifier.size(80.dp))
    PartialBottomSheet(uiState, viewModel, navController)
    Spacer(modifier = Modifier.size(40.dp))
}

@Composable
private fun LoadingScreen() {
    ShimmerEffect(
        modifier = Modifier
            .height(221.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.tertiary,
                RoundedCornerShape(12.dp)
            )
    )
    Spacer(modifier = Modifier.size(10.dp))
    ShimmerEffect(
        modifier = Modifier
            .height(270.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.tertiary,
                RoundedCornerShape(12.dp)
            )
    )
}

@Composable
private fun ErrorScreen(uiState: MoneyUiState, viewModel: MoneyViewModel) {
    ErrorScreenTemplate(uiState, viewModel)
}

@Composable
private fun TitleText(title: String) {
    Text(
        title,
        color = MaterialTheme.colorScheme.onTertiary,
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 6.dp)
            .fillMaxWidth(),
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun Price(price: MoneyModel?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 18.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.money_icon_black),
            contentDescription = "Custom Money Icon",
            Modifier.size(26.dp)
        )
        Text(
            text = "${price?.bid?.toFloat()?.toDecimalFormatTreePlaces()}",
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "${price?.pctChange}%",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = negativeValueColor("${price?.pctChange}")
        )
    }
}

@Composable
private fun MaxMin(price: MoneyModel?) {
    Row(
        modifier = Modifier
            .padding(start = 18.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.arrow_up),
            contentDescription = "Custom Money Icon",
        )
        Text(
            text = "${price?.high?.toFloat()?.toDecimalFormatTreePlaces()}",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.size(5.dp))
        Image(
            painter = painterResource(id = R.drawable.arrow_down),
            contentDescription = "Custom Money Icon",
        )
        Text(
            text = "${price?.low?.toFloat()?.toDecimalFormatTreePlaces()}",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun NameMoney(uiState: MoneyUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${uiState.coin?.label} - ${uiState.price?.code}",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        Image(
            painter = painterResource(uiState.coin?.image!!),
            contentDescription = "image",
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .size(27.dp)

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleSelectableChips(viewModel: MoneyViewModel?, dailyChart: String) {
    val list = listOf(
        R.string.money_home_7_days to 7,
        R.string.money_home_1_months to 30,
        R.string.money_home_3_months to 90,
        R.string.money_home_6_months to 180,
        R.string.money_home_1_year to 365
    )

    val initialIndex = list.indexOfFirst { it.second == dailyChart.toInt() }.coerceAtLeast(0)
    val itemPosition = remember { mutableIntStateOf(initialIndex) }
    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(CornerSize(13.dp)),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            list.forEachIndexed { index, period ->
                FilterChip(
                    selected = itemPosition.intValue == index,
                    onClick = {
                        if (itemPosition.intValue != index) {
                            viewModel?.setPeriodChart(period.second.toString())
                        }
                    },
                    label = { Text(stringResource(period.first), fontSize = 11.sp) },
                    border = null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        }
    }
}

@Composable
private fun Chart(
    listItems: List<Entry>?,
    dailyChart: String
) {
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
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.size(6.dp))
        TitleText(stringResource(R.string.money_home_last_days, dailyChart.toInt()))
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, bottom = 15.dp)
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
                        textSize = 11f // Set the font size for X axis labels
                        valueFormatter = DateValueFormatter()
                        axisMinimum = 0f
                        labelRotationAngle = 0f
                        isGranularityEnabled = true
                    }

                    axisLeft.apply {
                        setDrawGridLines(false) // Disable grid lines
                        setDrawAxisLine(false) // Disable axis line
                        textSize = 11f // Set the font size for Y axis labels
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PartialBottomSheet(
    uiState: MoneyUiState,
    viewModel: MoneyViewModel,
    navController: NavHostController
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            ButtonWithLabel(
                R.string.money_home_other_currencies,
                MaterialTheme.colorScheme.primary,
                onClick = {
                    showBottomSheet = true
                })
            Spacer(modifier = Modifier.size(10.dp))
            ButtonWithLabel(
                R.string.money_home_converter,
                MaterialTheme.colorScheme.tertiary,
                onClick = {
                    navController.navigate(WalletScreen.Calculator.name)
                })
            Spacer(modifier = Modifier.size(10.dp))
            ButtonWithIcon(painterResource(id = if (uiState.coin?.isFavorite == true) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24),
                onClick = {
                    viewModel.setFavoriteCoin()
                })
            Spacer(modifier = Modifier.size(10.dp))
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxHeight(),
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false }
            ) {
                LazyColumn {
                    items(uiState.listCoin ?: return@LazyColumn) { coin ->
                        CardList(coin, uiState.typeMoney) {

                            if (coin.typeMoney != uiState.typeMoney)
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    viewModel.selectCoin(coin)
                                    showBottomSheet = false
                                }

                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ButtonWithLabel(text: Int, color: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier.width(160.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(color),
        elevation = ButtonDefaults.elevatedButtonElevation(0.dp),
    ) {
        Text(text = stringResource(text), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ButtonWithIcon(image: Painter, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        OutlinedCard(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(CornerSize(23.dp)),
        ) {
            Image(
                painter = image,
                contentDescription = "Custom Money Icon",
                Modifier
                    .padding(12.dp)
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun CardList(coinModel: CoinModel, symbol: TypeMoney, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CornerSize(10.dp)),
        colors = CardDefaults.cardColors(containerColor = if (coinModel.typeMoney == symbol) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            1.dp,
            if (coinModel.typeMoney == symbol) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
            Image(
                painter = painterResource(coinModel.image),
                contentDescription = "image",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(25.dp)
                    .clip(RoundedCornerShape(CornerSize(6.dp)))
                    .align(alignment = Alignment.CenterVertically)

            )
            Text(
                text = coinModel.label, modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
            )
            Image(
                painter = painterResource(id = if (coinModel.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24),
                contentDescription = "image",
                modifier = Modifier
                    .padding(8.dp)
                    .size(25.dp)
                    .clip(RoundedCornerShape(CornerSize(6.dp)))
                    .align(alignment = Alignment.CenterVertically)

            )
        }
    }
}

@Composable
private fun negativeValueColor(value: String?): Color {
    return if (value?.contains("-") == true) {
        MaterialTheme.colorScheme.onError
    } else {
        MaterialTheme.colorScheme.primary
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        MoneyView(rememberNavController(), viewModel())
    }
}