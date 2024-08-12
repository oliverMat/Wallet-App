package com.oliver.wallet.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.HomeView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletTheme {
                HomeView().View()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        HomeView().View()
    }
}