package com.oliver.wallet.ui.view.calculator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oliver.wallet.R
import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.common.ShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.TypeMoney
import com.oliver.wallet.util.toDecimalFormat

@Composable
fun CalculatorView(viewModel: MoneyViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary)
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState.connectionState) {
            ConnectionStatus.Success -> {
                Spacer(modifier = Modifier.size(50.dp))
                BoxResult(uiState.getCalculateResult())
                Spacer(modifier = Modifier.size(20.dp))
                Image(
                    painter = painterResource(id = R.drawable.equal_icon),
                    contentDescription = null,
                    Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.size(20.dp))
                BoxCurrentPrice(uiState.price)
                Spacer(modifier = Modifier.size(20.dp))
                Icon(
                    Icons.Default.Clear,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(30.dp),
                    contentDescription = null,
                )
            }

            ConnectionStatus.Loading -> {
                Spacer(modifier = Modifier.size(50.dp))
                ShimmerEffect(
                    modifier = Modifier
                        .width(150.dp)
                        .height(71.dp)
                        .background(
                            MaterialTheme.colorScheme.tertiary,
                            RoundedCornerShape(10.dp)
                        )
                )
                Spacer(modifier = Modifier.size(20.dp))
                Image(
                    painter = painterResource(id = R.drawable.equal_icon),
                    contentDescription = null,
                    Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.size(20.dp))
                ShimmerEffect(
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                        .background(
                            MaterialTheme.colorScheme.tertiary,
                            RoundedCornerShape(10.dp)
                        )
                )
                Spacer(modifier = Modifier.size(20.dp))
                Icon(
                    Icons.Default.Clear,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(30.dp),
                    contentDescription = null,
                )
            }

            ConnectionStatus.Error -> {

            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        SimpleOutlinedTextFieldSample(viewModel)
        SingleSelectChipList(viewModel, uiState)
    }
}

@Composable
fun BoxResult(calculate: Float?) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 15.dp, horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${calculate?.toDecimalFormat()}",
                fontSize = 35.sp,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 200.dp)
            )
        }
    }
}

@Composable
fun BoxCurrentPrice(price: MoneyModel?) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 15.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${price?.bid?.toFloat()?.toDecimalFormat()}",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = "${price?.code}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun SimpleOutlinedTextFieldSample(viewModel: MoneyViewModel) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.secondary, fontSize = 16.sp),
        label = { Text("Valor a converter", color = MaterialTheme.colorScheme.secondary) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.secondary
        ),
        onValueChange = { newText ->
            text = filterInputText(newText)
            viewModel.calculate(text)
        },
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.dialpad_icon),
                contentDescription = null,
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.Refresh,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        text = ""
                        viewModel.calculate(text)
                    }
            )
        }
    )
}

private fun filterInputText(input: String): String {
    if (input.length == 1 && (input == "." || input == ",")) {
        return ""
    }
    val sanitizedInput = input.replace(",", "")

    val firstDotIndex = sanitizedInput.indexOf('.')

    return if (firstDotIndex != -1) {
        val beforeDot = sanitizedInput.substring(0, firstDotIndex + 1)
        val afterDot = sanitizedInput.substring(firstDotIndex + 1).replace(".", "")
        beforeDot + afterDot
    } else {
        sanitizedInput
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
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(top = 10.dp)
    ) {
        label.forEachIndexed { index, it ->
            val isSelected = it == selected
            Chip(
                colors = ChipDefaults.chipColors(backgroundColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary),
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
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        CalculatorView(MoneyViewModel())
    }
}