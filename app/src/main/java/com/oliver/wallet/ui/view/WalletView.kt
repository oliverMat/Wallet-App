package com.oliver.wallet.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oliver.wallet.R
import com.oliver.wallet.ui.view.calculator.CalculatorView
import com.oliver.wallet.ui.view.graphic.money.MoneyGraphicView
import com.oliver.wallet.ui.view.money.MoneyView
import com.oliver.wallet.ui.view.stock.StockView
import com.oliver.wallet.util.WalletScreen

sealed class Screen(val route: String, val label: Int, val icon: Int?) {
    data object Money : Screen(WalletScreen.Money.name, R.string.nav_name_money, R.drawable.money_icon)
    data object Stock : Screen(WalletScreen.Stock.name, R.string.nav_name_stock, R.drawable.show_chart_icon)
    data object Calculator : Screen(WalletScreen.Calculator.name, R.string.nav_name_calculator, null)
    data object MoneyGraphic : Screen(WalletScreen.MoneyGraphic.name, R.string.nav_name_money_graphic, null)
}

@Composable
fun WalletAppBar(
    currentScreen: String,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(currentScreen) },
        backgroundColor = MaterialTheme.colorScheme.secondary,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "stringResource(R.string.back_button)"
                )
            }
        }
    )
}

@Composable
fun WalletApp(navController: NavHostController = rememberNavController()) {
    val bottomNavItems = listOf(Screen.Money, Screen.Stock)

    Scaffold(
        topBar = {
            if (currentRoute(navController) !in bottomNavItems.map { it.route }) {
                WalletAppBar(
                    currentScreen = (currentRoute(navController)) ?: WalletScreen.Money.name,
                    navigateUp = { navController.navigateUp() }
                )
            }
        },
        bottomBar = {
            if (currentRoute(navController) in bottomNavItems.map { it.route }) {
                BottomNavigationBar(navController, bottomNavItems)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Money.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Money.route) { MoneyScreen(navController) }
            composable(Screen.Stock.route) { StockScreen(navController) }
            composable(Screen.Calculator.route) { CalculatorScreen(navController) }
            composable(Screen.MoneyGraphic.route) { MoneyGraphicScreen(navController) }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController, bottomNavItems: List<Screen>) {
    BottomNavigation {
        val currentRoute = currentRoute(navController)
        bottomNavItems.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = screen.icon!!),
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
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                alwaysShowLabel = false
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
private fun MoneyScreen(navController: NavHostController) {
    MoneyView(navController)
}

@Composable
private fun StockScreen(navController: NavHostController) {
    StockView(navController)
}

@Composable
private fun CalculatorScreen(navController: NavHostController) {
    CalculatorView(navController)
}

@Composable
private fun MoneyGraphicScreen(navController: NavHostController) {
    MoneyGraphicView(navController)
}