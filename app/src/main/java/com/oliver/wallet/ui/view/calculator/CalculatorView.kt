package com.oliver.wallet.ui.view.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oliver.wallet.R
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.common.ShimmerEffect
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.ConnectionStatus
import com.oliver.wallet.util.formatCurrencyInput
import com.oliver.wallet.util.formatPercentage
import com.oliver.wallet.util.toDecimalFormatTwoPlaces
import kotlinx.coroutines.launch

@Composable
fun CalculatorView(viewModel: MoneyViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        when (uiState.connectionState) {
            ConnectionStatus.Success -> SuccessScreen(uiState, viewModel, Modifier.weight(1f))

            ConnectionStatus.Loading -> LoadingScreen()

            ConnectionStatus.Error -> ErrorScreen()
        }
    }
}

@Composable
private fun SuccessScreen(uiState: MoneyUiState, viewModel: MoneyViewModel, modifier: Modifier) {
    var checked by remember { mutableStateOf(true) }

    Spacer(modifier = Modifier.size(40.dp))
    SimpleOutlinedTextFieldSample(viewModel, modifier)
    SwitchWithLabel(checked) {
        checked = it
    }
    AnimatedVisibility(visible = checked) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            Template(
                "Cotação",
                "1 ${uiState.price?.code} = ${
                    uiState.price?.bid?.toFloat()?.toDecimalFormatTwoPlaces()
                } BRL"
            ) { }
            Template(
                "IOF ${uiState.calculate.iof.formatPercentage()}",
                "R$ ${uiState.getIof().toDecimalFormatTwoPlaces()}"
            ) { ImageIcon(painterResource(R.drawable.add_24)) }
            Template(
                "Spread ${uiState.calculate.taxa.formatPercentage()}",
                "R$ ${uiState.getTaxa().toDecimalFormatTwoPlaces()}"
            ) { ImageIcon(painterResource(R.drawable.add_24)) }
            Divider(Modifier.padding(horizontal = 20.dp, vertical = 5.dp))
            Template(
                "Total",
                "R$ ${uiState.getResultsWithAllTax().toDecimalFormatTwoPlaces()}"
            ) { ImageIcon(painterResource(R.drawable.equal_24dp)) }
            PartialBottomSheet(uiState, viewModel)
        }
    }
    Spacer(modifier = Modifier.size(10.dp))
    ResultCalculate(uiState)
}

@Composable
private fun LoadingScreen() {
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
}

@Composable
private fun ErrorScreen() {

}

@Composable
private fun SimpleOutlinedTextFieldSample(viewModel: MoneyViewModel, modifier: Modifier) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var textFieldValue by remember { mutableStateOf(TextFieldValue("0,00")) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()

        textFieldValue = textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        OutlinedTextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = textFieldValue,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.secondary, fontSize = 25.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary
            ),
            onValueChange = { newValue ->
                val formattedText = formatCurrencyInput(newValue.text)
                textFieldValue = newValue.copy(
                    text = formattedText,
                    selection = TextRange(formattedText.length)
                )
                viewModel.calculate(formattedText)
            },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.brasil_flag),
                    contentDescription = null,
                )
            },
        )
    }
}

@Composable
private fun SwitchWithLabel(state: Boolean, onStateChange: (Boolean) -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch,
                onClick = {
                    onStateChange(!state)
                }
            )
    ) {
        Text(
            "Taxas",
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Switch(
            checked = state,
            onCheckedChange = {
                onStateChange(it)
            }
        )
    }
}

@Composable
private fun Template(title: String, value: String, imageIcon: @Composable () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            imageIcon()
            Spacer(Modifier.size(8.dp))
            Text(
                title,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        Text(
            value,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun ImageIcon(image: Painter) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        Image(
            painter = image,
            contentDescription = "image",
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun ButtonWithLabel(text: Int, textColor: Color, background: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier.width(160.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(background),
        elevation = ButtonDefaults.elevatedButtonElevation(0.dp),
    ) {
        Text(
            text = stringResource(text),
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PartialBottomSheet(
    uiState: MoneyUiState,
    viewModel: MoneyViewModel
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { newState -> newState != SheetValue.Hidden })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ButtonWithLabel(
            R.string.calculator_edit_tax,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.background
        ) {
            showBottomSheet = true
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.background,
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
                ) {
                    BottomSheetTemplate("IOF", uiState.calculate.iof, uiState.getIof().toDecimalFormatTwoPlaces()) { value -> viewModel.updateIof(value) }
                    Spacer(modifier = Modifier.size(30.dp))
                    BottomSheetTemplate("Spread", uiState.calculate.taxa, uiState.getTaxa().toDecimalFormatTwoPlaces()) { value -> viewModel.updateTaxa(value) }
                    Spacer(modifier = Modifier.size(40.dp))
                    ButtonWithLabel(
                        R.string.calculator_save,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary
                    ) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                        }
                    }
                    Spacer(modifier = Modifier.size(60.dp))
                }
            }
        }
    }
}

@Composable
private fun BottomSheetTemplate(title: String, value: Float,result: String, onUpdate: (Float) -> Unit) {
    Text(
        title,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Slider(
        value = value,
        onValueChange = { onUpdate(it) },
        modifier = Modifier.padding(horizontal = 20.dp),
        valueRange = 0f..0.07f
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            Text(
                value.formatPercentage(),
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(20.dp))
            Text("R$ $result")
        }
    }
}

@Composable
private fun ResultCalculate(uiState: MoneyUiState) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            "Voce vai receber",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .imePadding()
        ) {
            Image(
                painter = painterResource(uiState.coin?.image!!),
                contentDescription = "image",
                modifier = Modifier
                    .size(27.dp)
            )
            Text(
                uiState.getCalculateResult(),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 20.sp,
                modifier = Modifier.padding(10.dp)
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        CalculatorView(MoneyViewModel(viewModel(), viewModel()))
    }
}