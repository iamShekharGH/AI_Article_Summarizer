package com.shekharhandigol.aiarticlesummarizer.ui.homeScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
        val currentDestination = navBackStackEntry?.destination
//navController.currentDestination?.route == item.destinations.toString(),
        //currentDestination == item.destinations

        bottomNavItems.forEach { item ->
            println("item.destinations.toString(): ${item.destinations}")
            println("navController.currentDestination: ${navController.currentDestination}")
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentDestination?.toString()
                    ?.contains("." + item.destinations.toString())
                    ?: false,
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

        /*NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Saved Articles"
                )
            },
            label = { Text("Home") },
            selected = false,
            onClick = {
                navController.navigate(Destinations.Saved) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            })
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = "Home") },
            label = { Text("Search") },
            selected = true, // Assuming this is the current screen
            onClick = {
                navController.navigate(Destinations.Home) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Home") },
            label = { Text("List") },
            selected = true, // Assuming this is the current screen
            onClick = {
                navController.navigate(Destinations.List) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = false,
            onClick = {
                navController.navigate(Destinations.Settings) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )*/
    }
}

sealed class BottomNavigationBarDestination(val route: String) {
    data object Home : BottomNavigationBarDestination("home")
    data object Search : BottomNavigationBarDestination("search")
    data object Saved : BottomNavigationBarDestination("saved")
    data object Settings : BottomNavigationBarDestination("settings")
    data object List : BottomNavigationBarDestination("list")
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val destinations: Destinations
)

val bottomNavItems = listOf(
    BottomNavItem(
        "Home",
        Icons.Filled.Home,
        BottomNavigationBarDestination.Saved.route,
        Destinations.Home
    ),
    BottomNavItem(
        "Search",
        Icons.Filled.Search,
        BottomNavigationBarDestination.Search.route,
        Destinations.Search
    ),
    BottomNavItem(
        "List",
        Icons.AutoMirrored.Filled.List,
        BottomNavigationBarDestination.List.route,
        Destinations.List
    ),
    BottomNavItem(
        "Local Search",
        Icons.Filled.Search,
        BottomNavigationBarDestination.Home.route,
        Destinations.LocalSearch
    ),
    BottomNavItem(
        "Settings",
        Icons.Filled.Settings,
        BottomNavigationBarDestination.Settings.route,
        Destinations.Settings
    )
)
