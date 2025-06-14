package com.nargok.sakemap.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nargok.sakemap.ui.screens.SimpleListScreen
import com.nargok.sakemap.ui.screens.SimpleRecordScreen

@Composable
fun SakeMapNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.LIST
    ) {
        composable(NavRoutes.LIST) {
            SimpleListScreen(
                onNavigateToRecord = {
                    navController.navigate(NavRoutes.RECORD)
                }
            )
        }
        
        composable(NavRoutes.RECORD) {
            SimpleRecordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}