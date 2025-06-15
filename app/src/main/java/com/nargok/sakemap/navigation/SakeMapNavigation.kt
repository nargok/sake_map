package com.nargok.sakemap.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nargok.sakemap.presentation.ui.screens.SimpleListScreen
import com.nargok.sakemap.presentation.ui.screens.SimpleMapScreen
import com.nargok.sakemap.presentation.ui.screens.SimpleRecordScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SakeMapNavigation(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.MAP,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.RECORD) {
                SimpleRecordScreen()
            }
            
            composable(NavRoutes.MAP) {
                SimpleMapScreen()
            }
            
            composable(NavRoutes.LIST) {
                SimpleListScreen()
            }
        }
    }
}