
package com.turing.campsite.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

import com.turing.campsite.ui.components.EntrantBottomBar

/**
 * Entrant destinations that display the shared bottom navigation bar.
 */
private val entrantBottomBarRoutes = setOf(
    Routes.ENTRANT_HOME,
    Routes.ENTRANT_CAMPSITE_LIST,
    Routes.ENTRANT_QR_CODE,
    Routes.ENTRANT_NOTIFICATION,
    Routes.ENTRANT_PROFILE
)

/**
 * Provides shared application layout components.
 *
 * Displays navigation bars according to the current destination
 * and provides padding for the screen content.
 */
@Composable
fun AppScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    // Observe the currently displayed navigation destination.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in entrantBottomBarRoutes) {
                EntrantBottomBar(
                    selectedRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(
                                navController.graph.findStartDestination().id
                            ) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
