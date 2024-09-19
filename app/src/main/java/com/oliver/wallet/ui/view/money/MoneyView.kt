package com.oliver.wallet.ui.view.money

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringArrayResource
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
import com.oliver.wallet.data.model.MoneyUiState
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        SingleSelectChipList(viewModel, uiState)
        when (uiState.connectionState) {
            ConnectionStatus.Success -> {
                PriceBox(uiState.price, navController)
                VariantBox(uiState.price)
                MoneyChart(uiState.chart, navController)
                DateBox(uiState.price)
            }

            ConnectionStatus.Loading -> {
                EffectShimmerEffect()
            }

            ConnectionStatus.Error -> {

            }
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

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        label.forEachIndexed { index, it ->
            val isSelected = it == selected
            Chip(
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

@Composable
fun CustomButton(image: Int, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "Custom Money Icon",
        )
    }
}

@Composable
fun PriceBox(price: MoneyModel?, navController: NavHostController) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(20.dp)
    ) {
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
            Spacer(modifier = Modifier.size(20.dp))
            OutlinedButton(border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                onClick = {
                    navController.navigate(WalletScreen.Calculator.name)
                }) {
                Text(
                    "Calculadora",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
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
fun VariantBox(price: MoneyModel?) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Variação do dia:",
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.background
            )
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
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
        }
    }
}

@Composable
fun DateBox(price: MoneyModel?) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Ultima atualização:",
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.background
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "${price?.create_date?.dataFormat()}",
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
fun MoneyChart(listItems: List<Entry>?, navController: NavHostController) {
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
            .padding(5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Ultimos 7 dias",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.background,
            )
            AndroidView(
                modifier = Modifier
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
            Spacer(modifier = Modifier.size(25.dp))
            OutlinedButton(border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                onClick = { }) {
                Text(
                    "Expandir grafico",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMoreOptions(navController: NavHostController) {

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

//    CustomButton(onClick = {
//        showBottomSheet = true
//    })

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
fun EffectShimmerEffect() {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(15.dp)
    ) {
        Column {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(10.dp)
                    )
            )
        }
    }
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(15.dp)
    ) {
        Column {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(10.dp)
                    )
            )
        }
    }
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(15.dp)
    ) {
        Column {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(10.dp)
                    )
            )
        }
    }
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(15.dp)
    ) {
        Column {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
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