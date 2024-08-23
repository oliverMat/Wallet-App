package com.oliver.wallet.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oliver.wallet.R
import com.oliver.wallet.ui.theme.WalletTheme
import com.oliver.wallet.ui.view.stock.StockView
import com.oliver.wallet.ui.view.money.MoneyView

sealed class Screen(val route: String, val label: Int, val icon: Int) {
    data object Money : Screen("money", R.string.nav_name_money, R.drawable.money_icon)
    data object Stock : Screen("stock", R.string.nav_name_stock, R.drawable.show_chart_icon)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletTheme {
                MyApp()
            }
        }
    }
}

@Composable
private fun MyApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Money.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Money.route) { MoneyScreen() }
            composable(Screen.Stock.route) { StockScreen() }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController) {
    val items = listOf(Screen.Money, Screen.Stock)
    BottomNavigation {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = screen.icon),
                        contentDescription = "Custom Icon",
                    )
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
                label = {
                    Text(
                        stringResource(screen.label),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
private fun MoneyScreen() {
    MoneyView().View()
}

@Composable
private fun StockScreen() {
    StockView().View()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletTheme {
        MyApp()
    }
}