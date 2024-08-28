package com.oliver.wallet.ui.view.money

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.oliver.wallet.R
import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.common.ShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.DateValueFormatter
import com.oliver.wallet.util.TypeMoney
import com.oliver.wallet.util.WalletScreen
import com.oliver.wallet.util.dataFormat
import com.oliver.wallet.util.toDecimalFormat
import kotlinx.coroutines.launch

@Composable
fun MoneyView(navController: NavHostController, viewModel: MoneyViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(top = 10.dp)) {
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
            when (uiState.connectionState) {
                ConnectionStatus.Success -> {
                    PriceBox(uiState.price)
                    MoneyChart(uiState.chart)
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

@Composable
fun GridButtons(viewModel: MoneyViewModel) {
    var selectedButton by remember { mutableStateOf(false) }

    Row {
        Spacer(modifier = Modifier.size(8.dp))
        SelectMoney(
            "Dolar/Real",
            if (!selectedButton) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            onClick = {
                selectedButton = false
                viewModel.selectMoneySymbol(TypeMoney.Dollar)
            },
        )
        Spacer(modifier = Modifier.size(10.dp))
        SelectMoney(
            "Euro/Real",
            if (selectedButton) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            onClick = {
                selectedButton = true
                viewModel.selectMoneySymbol(TypeMoney.Euro)
            },
        )
    }
}

@Composable
fun SelectMoney(
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        shape = RoundedCornerShape(50),
        modifier = Modifier.width(140.dp),
        onClick = onClick
    ) {
        Text(label)
    }
}

@Composable
fun MoreMenuTopButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(start = 10.dp, end = 10.dp, top = 7.dp, bottom = 7.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = R.drawable.more_icon),
            contentDescription = "Custom Money Icon",
        )
    }
}

@Composable
fun PriceBox(price: MoneyModel?) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .size(35.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.money_icon),
                        contentDescription = "Custom Money Icon",
                    )
                }
                Text(
                    text = "${price?.name}",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Text(
                text = "${price?.bid?.toFloat()?.toDecimalFormat()}",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.background
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = "%: ${price?.pctChange}",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = negativeValueColor("${price?.pctChange}")
            )
            Spacer(modifier = Modifier.size(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_up_24),
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
            Spacer(modifier = Modifier.size(15.dp))
            Text(
                text = "${price?.create_date?.dataFormat()}",
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
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
fun MoneyChart(listItems: List<Entry>?) {
    var lineData by remember { mutableStateOf(LineData()) }

    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val secondaryColor = MaterialTheme.colorScheme.secondary.toArgb()

    LaunchedEffect(listItems) {
        val dataSet = LineDataSet(listItems, "").apply {
            color = primaryColor
            setCircleColor(primaryColor)
            lineWidth = 2f
            circleRadius = 4f
            setDrawFilled(true)
            fillColor = primaryColor
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            valueTextSize = 14f
            valueTextColor = secondaryColor
        }
        lineData = LineData(dataSet)
    }

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .size(35.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.show_chart_icon),
                        contentDescription = "Custom Money Icon",
                    )
                }
                Text(
                    text = "Ultimos 7 dias",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            AndroidView(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxSize()
                    .height(400.dp),
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
                            labelRotationAngle = -45f
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMoreOptions(navController: NavHostController) {

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    MoreMenuTopButton(onClick = {
        showBottomSheet = true
    })

    if (showBottomSheet) {
        ModalBottomSheet(
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            Column {
                LabelOptions("Mais Opções:")
                SelectOptions("Calculadora", onClick = {
                    scope
                        .launch { sheetState.hide() }
                        .invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                navController.navigate(WalletScreen.Calculator.name)
                            }
                        }
                })
                Spacer(modifier = Modifier.size(10.dp))
                SelectOptions("Expandir Grafico", onClick = {
                    scope
                        .launch { sheetState.hide() }
                        .invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                navController.navigate(WalletScreen.MoneyGraphic.name)
                            }
                        }
                })
                Spacer(modifier = Modifier.size(30.dp))
                LabelOptions("Moedas:")
                Spacer(modifier = Modifier.size(60.dp))
            }

        }
    }
}

@Composable
fun LabelOptions(label: String) {
    Text(
        text = label,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
    )
}

@Composable
fun SelectOptions(
    label: String,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(15.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
fun PriceShimmerEffect() {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .size(35.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.money_icon),
                        contentDescription = "Custom Money Icon",
                    )
                }
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .padding(start = 10.dp, end = 50.dp)
                        .background(
                            MaterialTheme.colorScheme.tertiary,
                            RoundedCornerShape(10.dp)
                        )
                )
            }
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .padding(horizontal = 10.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(10.dp)
                    )
            )
        }
    }
}

@Composable
fun GraphicShimmerEffect() {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .size(35.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.show_chart_icon),
                        contentDescription = "Custom Money Icon",
                    )
                }
                Text(
                    text = "Ultimos 7 dias",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
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
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        MoneyView(rememberNavController(), MoneyViewModel())
    }
}