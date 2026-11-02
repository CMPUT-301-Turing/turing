package com.turing.campsite

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.turing.campsite.navigation.Routes
import com.turing.campsite.navigation.AppScaffold
import com.turing.campsite.navigation.entrantGraph
import com.turing.campsite.navigation.ownerGraph
import com.turing.campsite.navigation.adminGraph

import com.turing.campsite.ui.screens.shared.LoginScreen
import com.turing.campsite.ui.screens.shared.RegisterScreen
import com.turing.campsite.ui.screens.shared.LogOutScreen

/**
 * Root navigation host for the Turing Campsite application.
 *
 * Initializes the navigation controller and registers
 * shared and role-specific navigation destinations.
 */
@Composable
fun TuringCampsiteApp() {
    val navController = rememberNavController()

    AppScaffold(navController) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Shared screens
            composable(Routes.LOGIN) {
                LoginScreen(navController)
            }

            composable(Routes.REGISTER) {
                RegisterScreen(navController)
            }

            composable(Routes.LOG_OUT) {
                LogOutScreen(navController)
            }

            // Role-specific navigation
            entrantGraph(navController)
            ownerGraph(navController)
            adminGraph(navController)
        }
    }
}
