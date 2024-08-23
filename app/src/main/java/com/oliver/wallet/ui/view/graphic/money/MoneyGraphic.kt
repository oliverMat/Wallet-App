package com.oliver.wallet.ui.view.graphic.money

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.oliver.wallet.ui.theme.WalletTheme

@Composable
fun MoneyGraphicView(navController: NavHostController) {

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        MoneyGraphicView(rememberNavController())
    }
}