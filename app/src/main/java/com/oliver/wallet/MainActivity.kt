package com.oliver.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.WalletApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletTheme {
                WalletApp()
            }
        }
    }
}