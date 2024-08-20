package com.oliver.wallet.ui.view.money

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.oliver.wallet.R
import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.ui.view.ShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.DateValueFormatter
import com.oliver.wallet.util.TypeMoney
import com.oliver.wallet.util.dataFormat
import com.oliver.wallet.util.toDecimalFormat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SingleSelectChipList(viewModel: MoneyViewModel) {
    val listMoneyLabel = stringArrayResource(R.array.list_money_label).toList()

    var selectedChip by remember { mutableStateOf<String?>(listMoneyLabel[0]) }

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                listMoneyLabel.forEachIndexed { index, it ->
                    val isSelected = it == selectedChip
                    Chip(
                        onClick = {
                            selectedChip = if (isSelected) selectedChip else it

                            viewModel.selectMoneySymbol(
                                when (index) {
                                    0 -> TypeMoney.Dollar
                                    1 -> TypeMoney.Euro
                                    else -> TypeMoney.Bitcoin
                                }
                            )
                        },
                        colors = ChipDefaults.chipColors(backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = it,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Price(price: MoneyModel?) {
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

@Composable
fun negativeValueColor(value: String?): Color {
    return if (value?.contains("-") == true) {
        MaterialTheme.colorScheme.onError
    } else {
        MaterialTheme.colorScheme.primary
    }
}