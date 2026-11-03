package com.turing.campsite.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel

import com.turing.campsite.ui.screens.shared.LoginScreen
import com.turing.campsite.ui.screens.shared.RegisterScreen
import com.turing.campsite.ui.screens.shared.LogOutScreen
import com.turing.campsite.ui.screens.shared.UserProfileScreen
import com.turing.campsite.ui.screens.shared.UserEditProfileScreen
import com.turing.campsite.ui.screens.shared.UserView
import com.turing.campsite.auth.LoginViewModel

/**
 * Registers shared navigation destinations.
 *
 * These screens are not exclusive to the entrant
 * or campsite owner roles.
 */
fun NavGraphBuilder.sharedGraph(
    navController: NavHostController
) {
    // Authentication
    composable(Routes.LOGIN) {
        val loginViewModel: LoginViewModel = viewModel()

        LoginScreen(
            navController = navController,
            loginState = loginViewModel.loginState,
            onRetry = {
                loginViewModel.identifyDevice()
            }
        )
    }

    composable(Routes.REGISTER) {
        RegisterScreen(navController)
    }

    composable(Routes.LOG_OUT) {
        LogOutScreen(navController)
    }

    // User profile when accessed from Entrant view
    composable(Routes.ENTRANT_PROFILE) {
        UserProfileScreen(
            currentView = UserView.ENTRANT,
            onEditProfileClick = {
                navController.navigate(Routes.USER_EDIT_PROFILE)
            },
            onSwitchViewClick = {
                navController.navigate(Routes.OWNER_HOME)
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    // User profile when accessed from Owner view
    composable(Routes.OWNER_PROFILE) {
        UserProfileScreen(
            currentView = UserView.OWNER,
            onEditProfileClick = {
                navController.navigate(Routes.USER_EDIT_PROFILE)
            },
            onSwitchViewClick = {
                navController.navigate(Routes.ENTRANT_HOME)
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    // Shared edit profile screen
    composable(Routes.USER_EDIT_PROFILE) {
        UserEditProfileScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}
