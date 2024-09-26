package com.oliver.wallet.ui.view

import android.content.pm.ActivityInfo
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oliver.wallet.R
import com.oliver.wallet.ui.view.calculator.CalculatorView
import com.oliver.wallet.ui.view.common.LockScreenOrientation
import com.oliver.wallet.ui.view.graphic.money.MoneyGraphicView
import com.oliver.wallet.ui.view.money.MoneyView
import com.oliver.wallet.ui.view.stock.StockView
import com.oliver.wallet.ui.viewmodel.MoneyViewModel
import com.oliver.wallet.util.WalletScreen

sealed class Screen(val route: String, val label: Int, val icon: Int?) {
    data object Money : Screen(WalletScreen.Money.name, R.string.nav_name_money, R.drawable.money_icon)
    data object Stock : Screen(WalletScreen.Stock.name, R.string.nav_name_stock, R.drawable.show_chart_icon)
    data object Calculator : Screen(WalletScreen.Calculator.name, R.string.nav_name_calculator, null)
    data object MoneyGraphic : Screen(WalletScreen.MoneyGraphic.name, R.string.nav_name_money_graphic, null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletAppBar(
    currentScreen: String,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    showNavigationIcon: Boolean
) {
    TopAppBar(
        title = { Text(currentScreen, fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.tertiary),
        modifier = modifier,
        navigationIcon = {
            if (showNavigationIcon) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Composable
fun WalletApp(navController: NavHostController = rememberNavController(), viewModel: MoneyViewModel = viewModel()) {
    val bottomNavItems = listOf(Screen.Money, Screen.Stock)

    Scaffold(
        topBar = {
            WalletAppBar(
                currentScreen = (currentRoute(navController)) ?: WalletScreen.Money.name,
                navigateUp = { navController.navigateUp() },
                showNavigationIcon = showNavigationIcon(navController, bottomNavItems)
            )
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
            composable(Screen.Money.route) { MoneyScreen(navController, viewModel) }
            composable(Screen.Stock.route) { StockScreen(navController) }
            composable(Screen.Calculator.route) { CalculatorScreen(viewModel) }
            composable(Screen.MoneyGraphic.route) { MoneyGraphicScreen(viewModel) }
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
                modifier = Modifier.background(MaterialTheme.colorScheme.tertiary),
                label = {
                    Text(
                        stringResource(screen.label),
                        color = MaterialTheme.colorScheme.secondary
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
                selectedContentColor = MaterialTheme.colorScheme.secondary,
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
private fun showNavigationIcon(navController: NavHostController, bottomNavItems: List<Screen>): Boolean {
    return (currentRoute(navController) !in bottomNavItems.map { it.route })
}

@Composable
private fun MoneyScreen(navController: NavHostController, viewModel: MoneyViewModel) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    MoneyView(navController, viewModel)
}

@Composable
private fun StockScreen(navController: NavHostController) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    StockView(navController)
}

@Composable
private fun CalculatorScreen(viewModel: MoneyViewModel) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    CalculatorView(viewModel)
}

@Composable
private fun MoneyGraphicScreen(viewModel: MoneyViewModel) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    MoneyGraphicView(viewModel)
}