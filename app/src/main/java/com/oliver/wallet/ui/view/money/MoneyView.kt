package com.oliver.wallet.ui.view.money

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.oliver.wallet.R
import com.oliver.wallet.data.network.MoneyModel
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.data.room.CoinModel
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.common.ComposableLifecycle
import com.oliver.wallet.ui.view.common.ErrorScreenTemplate
import com.oliver.wallet.ui.view.common.ShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.DateValueFormatter
import com.oliver.wallet.util.TypeMoney
import com.oliver.wallet.util.WalletScreen
import com.oliver.wallet.util.toDecimalFormat
import kotlinx.coroutines.launch

@Composable
fun MoneyView(
    navController: NavHostController,
    viewModel: MoneyViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LifeCycle(viewModel)

    when (uiState.connectionState) {
        ConnectionStatus.Success -> SuccessScreen(uiState, navController, viewModel)

        ConnectionStatus.Loading -> LoadingScreen()

        ConnectionStatus.Error -> ErrorScreen(uiState, viewModel)
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

@Composable
private fun SuccessScreen(
    uiState: MoneyUiState,
    navController: NavHostController,
    viewModel: MoneyViewModel
) {
    PrincipalColumn {
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            ),
            modifier = Modifier.padding(8.dp)
        ) {
            Row(modifier = Modifier.height(205.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    TitleText(stringResource(R.string.money_home_current_quote))
                    Price(uiState.price)
                    Spacer(modifier = Modifier.size(10.dp))
                    TitleText(stringResource(R.string.money_home_variation_of_day))
                    MaxMin(uiState.price)
                    Spacer(modifier = Modifier.size(10.dp))
                    TitleText(stringResource(R.string.money_home_coin))
                    Text(
                        "${uiState.coin?.label} - ${uiState.price?.code}",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 18.dp, bottom = 10.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Image(
                        modifier = Modifier
                            .padding(vertical = 10.dp, horizontal = 18.dp)
                            .clickable {
                                viewModel.setFavoriteCoin()
                            },
                        painter = if (uiState.coin?.isFavorite == true) painterResource(id = R.drawable.baseline_favorite_24) else painterResource(
                            id = R.drawable.baseline_favorite_border_24
                        ),
                        contentDescription = "Custom Money Icon",
                    )
                    Image(
                        painter = painterResource(uiState.coin?.image!!),
                        contentDescription = "image",
                        modifier = Modifier
                            .padding(vertical = 10.dp, horizontal = 15.dp)
                            .size(27.dp)

                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row {
            ButtonLabel(true,
                stringResource(R.string.money_home_converter),
                painterResource(id = R.drawable.calculate),
                onClick = { navController.navigate(WalletScreen.Calculator.name) })
            Spacer(modifier = Modifier.size(30.dp))
            ButtonLabel(true,
                stringResource(R.string.money_home_history),
                painterResource(id = R.drawable.bar_chart),
                onClick = { navController.navigate(WalletScreen.MoneyGraphic.name) })
        }
        Spacer(modifier = Modifier.size(15.dp))
        Chart(uiState.chart)
        Spacer(modifier = Modifier.size(35.dp))
        PartialBottomSheet(uiState, viewModel)
    }
}

@Composable
private fun LoadingScreen() {
    PrincipalColumn {
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
        Row {
            ButtonLabel(false,
                stringResource(R.string.money_home_converter),
                painterResource(id = R.drawable.calculate),
                onClick = { })
            Spacer(modifier = Modifier.size(30.dp))
            ButtonLabel(false,
                stringResource(R.string.money_home_history),
                painterResource(id = R.drawable.bar_chart),
                onClick = { })
        }
        Spacer(modifier = Modifier.size(15.dp))
        ShimmerEffect(
            modifier = Modifier
                .height(264.dp)
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    MaterialTheme.colorScheme.tertiary,
                    RoundedCornerShape(12.dp)
                )
        )
        Spacer(modifier = Modifier.size(35.dp))
        ButtonDialog(false, onClick = { })
    }
}

@Composable
private fun ErrorScreen(uiState: MoneyUiState, viewModel: MoneyViewModel) {
    ErrorScreenTemplate(uiState, viewModel)
}

@Composable
private fun PrincipalColumn(item: @Composable (modifier: Modifier) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp)
    ) {
        item(Modifier.weight(1f))
    }
}

@Composable
private fun TitleText(title: String) {
    Text(
        title,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.padding(10.dp),
        fontSize = 12.sp
    )
}

@Composable
private fun Price(price: MoneyModel?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.money_icon_white),
            contentDescription = "Custom Money Icon",
            Modifier.size(26.dp)
        )
        Text(
            text = "${price?.bid?.toFloat()?.toDecimalFormat()}",
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "-",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "${price?.pctChange}%",
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = negativeValueColor("${price?.pctChange}")
        )
    }
}

@Composable
private fun MaxMin(price: MoneyModel?) {
    Row(
        modifier = Modifier.padding(start = 11.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.arrow_up),
            contentDescription = "Custom Money Icon",
        )
        Text(
            text = "${price?.high?.toFloat()?.toDecimalFormat()}",
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
            text = "${price?.low?.toFloat()?.toDecimalFormat()}",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun ButtonLabel(enabled: Boolean, label: String, image: Painter, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        OutlinedCard(
            colors = CardDefaults.cardColors(containerColor = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.background),
        ) {
            Image(
                painter = image,
                contentDescription = "Custom Money Icon",
                Modifier
                    .padding(12.dp)
                    .size(26.dp)
            )
        }
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            label,
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun Chart(listItems: List<Entry>?) {
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
            containerColor = MaterialTheme.colorScheme.tertiary,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        TitleText(stringResource(R.string.money_home_last_days))
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
                        textSize = 10f // Set the font size for X axis labels
                        valueFormatter = DateValueFormatter()
                        axisMinimum = 0f
                        labelRotationAngle = 0f
                        isGranularityEnabled = true
                    }

                    axisLeft.apply {
                        setDrawGridLines(false) // Disable grid lines
                        setDrawAxisLine(false) // Disable axis line
                        textSize = 10f // Set the font size for Y axis labels
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
private fun PartialBottomSheet(uiState: MoneyUiState, viewModel: MoneyViewModel) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        ButtonDialog(true, onClick = {
            showBottomSheet = true
        })

        if (showBottomSheet) {
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.secondary,
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
private fun ButtonDialog(enabled: Boolean, onClick: () -> Unit) {
    Button(
        enabled = enabled,
        modifier = Modifier.width(250.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimary),
        elevation = ButtonDefaults.elevatedButtonElevation(1.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.background
        )
    ) {
        Text(stringResource(R.string.money_home_other_currencies))
    }
}

@Composable
fun CardList(coinModel: CoinModel, symbol: TypeMoney, onClick: () -> Unit) {

    OutlinedCard(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CornerSize(10.dp)),
        colors = if (coinModel.typeMoney == symbol) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary) else CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (coinModel.typeMoney == symbol) BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.background
        ) else BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
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
                painter = if (coinModel.isFavorite) painterResource(id = R.drawable.baseline_favorite_24) else painterResource(
                    id = R.drawable.baseline_favorite_border_24
                ),
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
        LoadingScreen()
    }
}