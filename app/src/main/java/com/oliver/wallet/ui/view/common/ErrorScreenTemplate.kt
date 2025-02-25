package com.oliver.wallet.ui.view.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oliver.wallet.R
import com.oliver.wallet.data.model.MoneyUiState
import com.oliver.wallet.ui.viewmodel.MoneyViewModel

@Composable
fun ErrorScreenTemplate(uiState: MoneyUiState, viewModel: MoneyViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.size(200.dp))
        Image(
            painter = painterResource(id = R.drawable.signal_disconnected_24dp),
            contentDescription = "Custom Money Icon",
            Modifier.size(66.dp)
        )
        Spacer(Modifier.size(15.dp))
        Text(stringResource(R.string.error_info), fontSize = 17.sp)
        Spacer(Modifier.size(7.dp))
        Text(stringResource(R.string.error_networking_info), fontSize = 13.sp)
        Spacer(Modifier.size(15.dp))
        Button(
            modifier = Modifier.width(250.dp),
            onClick = {
                viewModel.selectCoin(uiState.coin!!)
            }
        ) {
            Text(stringResource(R.string.error_reload))
        }
    }
}