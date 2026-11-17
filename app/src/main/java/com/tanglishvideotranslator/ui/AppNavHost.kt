package com.tanglishvideotranslator.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tanglishvideotranslator.ui.library.LibraryScreen
import com.tanglishvideotranslator.ui.player.PlayerScreen
import com.tanglishvideotranslator.ui.settings.SettingsScreen
import com.tanglishvideotranslator.viewmodel.LibraryViewModel

sealed class AppDestinations(val route: String, val label: String) {
    data object Library : AppDestinations("library", "Library")
    data object Recents : AppDestinations("recents", "Recents")
    data object Favorites : AppDestinations("favorites", "Favorites")
    data object Settings : AppDestinations("settings", "Settings")
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val destinations = listOf(
        AppDestinations.Library,
        AppDestinations.Recents,
        AppDestinations.Favorites,
        AppDestinations.Settings,
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = index == selectedIndex,
                        onClick = {
                            selectedIndex = index
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            val icon = when (destination) {
                                AppDestinations.Library -> Icons.Default.LibraryBooks
                                AppDestinations.Recents -> Icons.Default.History
                                AppDestinations.Favorites -> Icons.Default.Favorite
                                AppDestinations.Settings -> Icons.Default.Settings
                            }
                            Icon(icon, contentDescription = destination.label)
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestinations.Library.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestinations.Library.route) {
                LibraryScreen(viewModel = viewModel, onOpenPlayer = { uri ->
                    navController.navigate("player/$uri")
                })
            }
            composable(AppDestinations.Recents.route) {
                LibraryScreen(viewModel = viewModel, isRecents = true, onOpenPlayer = { uri ->
                    navController.navigate("player/$uri")
                })
            }
            composable(AppDestinations.Favorites.route) {
                LibraryScreen(viewModel = viewModel, isFavorites = true, onOpenPlayer = { uri ->
                    navController.navigate("player/$uri")
                })
            }
            composable(AppDestinations.Settings.route) {
                SettingsScreen()
            }
            composable("player/{uri}") { backStackEntry ->
                val uri = backStackEntry.arguments?.getString("uri") ?: ""
                PlayerScreen(uri = uri)
            }
        }
    }
}
