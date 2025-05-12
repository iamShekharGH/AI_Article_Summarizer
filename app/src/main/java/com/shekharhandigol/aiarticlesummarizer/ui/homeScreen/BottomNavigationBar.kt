package com.shekharhandigol.aiarticlesummarizer.ui.homeScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute?.contains("." + item.destinations.toString()) ?: false,
                onClick = {
                    navController.navigate(item.destinations) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val destinations: Destinations
)

val bottomNavItems = listOf(
    BottomNavItem(
        "Home",
        Icons.Filled.Home,
        Destinations.Home
    ),
    BottomNavItem(
        "Search",
        Icons.Filled.Search,
        Destinations.Search
    ),
    BottomNavItem(
        "List",
        Icons.AutoMirrored.Filled.List,
        Destinations.List
    ),
    BottomNavItem(
        "Favorite List",
        Icons.Filled.Star,
        Destinations.FavouriteList
    ),
    BottomNavItem(
        "Settings",
        Icons.Filled.Settings,
        Destinations.Settings
    )
)
