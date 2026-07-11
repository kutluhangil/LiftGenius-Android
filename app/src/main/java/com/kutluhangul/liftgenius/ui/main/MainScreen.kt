package com.kutluhangul.liftgenius.ui.main

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.calendar.CalendarScreen
import com.kutluhangul.liftgenius.ui.clients.AddClientScreen
import com.kutluhangul.liftgenius.ui.clients.ClientDetailScreen
import com.kutluhangul.liftgenius.ui.clients.ClientListScreen
import com.kutluhangul.liftgenius.ui.components.ambientGlow
import com.kutluhangul.liftgenius.ui.dashboard.DashboardScreen
import com.kutluhangul.liftgenius.ui.finance.FinanceScreen
import com.kutluhangul.liftgenius.ui.profile.ProfileScreen

object MainRoutes {
    const val DASHBOARD = "dashboard"
    const val CALENDAR = "calendar"
    const val CLIENTS = "clients"
    const val FINANCE = "finance"
    const val PROFILE = "profile"
    const val CLIENT_DETAIL = "client/{clientId}"
    const val CLIENT_ADD = "client_add"

    fun clientDetail(clientId: String) = "client/$clientId"
}

private data class TabItem(
    val route: String,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val tabs = listOf(
    TabItem(MainRoutes.DASHBOARD, R.string.tab_dashboard, Icons.Filled.Home, Icons.Outlined.Home),
    TabItem(MainRoutes.CALENDAR, R.string.tab_calendar, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    TabItem(MainRoutes.CLIENTS, R.string.tab_clients, Icons.Filled.Groups, Icons.Outlined.Groups),
    TabItem(MainRoutes.FINANCE, R.string.tab_finance, Icons.Filled.Payments, Icons.Outlined.Payments),
    TabItem(MainRoutes.PROFILE, R.string.tab_profile, Icons.Filled.Person, Icons.Outlined.Person),
)

/**
 * Main app shell: bottom navigation on phones, navigation rail on tablets
 * (window width ≥ 600dp).
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isTabRoute = tabs.any { it.route == currentRoute }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .ambientGlow(),
    ) {
        val useRail = maxWidth >= 600.dp
        if (useRail) {
            Row(Modifier.fillMaxSize()) {
                if (isTabRoute) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ) {
                        tabs.forEach { tab ->
                            NavigationRailItem(
                                selected = currentRoute == tab.route,
                                onClick = { navController.navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        if (currentRoute == tab.route) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = null,
                                    )
                                },
                                label = { Text(stringResource(tab.labelRes)) },
                            )
                        }
                    }
                }
                MainNavGraph(navController, Modifier.weight(1f))
            }
        } else {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    if (isTabRoute) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        ) {
                            tabs.forEach { tab ->
                                NavigationBarItem(
                                    selected = currentRoute == tab.route,
                                    onClick = { navController.navigateToTab(tab.route) },
                                    icon = {
                                        Icon(
                                            if (currentRoute == tab.route) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = null,
                                        )
                                    },
                                    label = { Text(stringResource(tab.labelRes)) },
                                )
                            }
                        }
                    }
                },
            ) { innerPadding ->
                MainNavGraph(navController, Modifier.padding(innerPadding))
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun MainNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = MainRoutes.DASHBOARD,
        modifier = modifier,
    ) {
        composable(MainRoutes.DASHBOARD) { DashboardScreen() }
        composable(MainRoutes.CALENDAR) { CalendarScreen() }
        composable(MainRoutes.CLIENTS) { entry ->
            val refreshRequested by entry.savedStateHandle
                .getStateFlow("refresh_clients", false)
                .collectAsState()
            ClientListScreen(
                onClientClick = { navController.navigate(MainRoutes.clientDetail(it)) },
                onAddClick = { navController.navigate(MainRoutes.CLIENT_ADD) },
                refreshRequested = refreshRequested,
                onRefreshConsumed = { entry.savedStateHandle["refresh_clients"] = false },
            )
        }
        composable(MainRoutes.FINANCE) { FinanceScreen() }
        composable(MainRoutes.PROFILE) { ProfileScreen() }
        composable(MainRoutes.CLIENT_DETAIL) {
            ClientDetailScreen(onBack = { navController.popBackStack() })
        }
        composable(MainRoutes.CLIENT_ADD) {
            AddClientScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_clients", true)
                    navController.popBackStack()
                },
            )
        }
    }
}
