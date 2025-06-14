package com.nargok.sakemap.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = NavRoutes.RECORD,
        icon = Icons.Default.Add,
        label = "記録"
    ),
    BottomNavItem(
        route = NavRoutes.MAP,
        icon = Icons.Default.LocationOn,
        label = "地図"
    ),
    BottomNavItem(
        route = NavRoutes.LIST,
        icon = Icons.Default.List,
        label = "一覧"
    )
)