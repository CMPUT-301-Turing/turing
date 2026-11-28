
package com.turing.campsite.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

import com.turing.campsite.ui.screens.shared.AppPolicyScreen
import com.turing.campsite.ui.screens.shared.LoginScreen
import com.turing.campsite.ui.screens.shared.RegisterScreen
import com.turing.campsite.ui.screens.shared.LogOutScreen
import com.turing.campsite.ui.screens.shared.UserProfileScreen
import com.turing.campsite.ui.screens.shared.UserEditProfileScreen
import com.turing.campsite.ui.screens.shared.UserView
import com.turing.campsite.ui.viewmodel.UserViewModel
import com.turing.campsite.data.auth.LocalCurrentUid

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
        LoginScreen(navController)
    }

    composable(Routes.REGISTER) {
        RegisterScreen(navController)
    }

    composable(Routes.LOG_OUT) {
        LogOutScreen(navController)
    }

    // User profile when accessed from Entrant view
    composable(Routes.ENTRANT_PROFILE) {
        val uid = LocalCurrentUid.current
        val userViewModel: UserViewModel = viewModel()

        // Reloads every time the profile is opened, so saved edits show up
        LaunchedEffect(uid) {
            userViewModel.load(uid)
        }

        UserProfileScreen(
            currentView = UserView.ENTRANT,
            user = userViewModel.user,
            onEditProfileClick = {
                navController.navigate(Routes.USER_EDIT_PROFILE)
            },
            onPolicyClick = {
                navController.navigate(Routes.APP_POLICY)
            },
            onSwitchViewClick = {
                navController.switchView(Routes.OWNER_HOME)
            },
            onBackClick = {
                navController.popBackStack()
            },
            onSwitchToAdminClick = {
                navController.switchView(Routes.ADMIN_HOME)
            }
        )
    }

    // User profile when accessed from Owner view
    composable(Routes.OWNER_PROFILE) {
        val uid = LocalCurrentUid.current
        val userViewModel: UserViewModel = viewModel()

        // Reloads every time the profile is opened, so saved edits show up
        LaunchedEffect(uid) {
            userViewModel.load(uid)
        }

        UserProfileScreen(
            currentView = UserView.OWNER,
            user = userViewModel.user,
            onEditProfileClick = {
                navController.navigate(Routes.USER_EDIT_PROFILE)
            },
            onPolicyClick = {
                navController.navigate(Routes.APP_POLICY)
            },
            onSwitchViewClick = {
                navController.switchView(Routes.ENTRANT_HOME)
            },
            onBackClick = {
                navController.popBackStack()
            },
            onSwitchToAdminClick = {
                navController.switchView(Routes.ADMIN_HOME)
            }
        )
    }

    // App policy and guidelines, opened from the profile screen
    composable(Routes.APP_POLICY) {
        AppPolicyScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    // Shared edit profile screen
    composable(Routes.USER_EDIT_PROFILE) {
        val uid = LocalCurrentUid.current
        val userViewModel: UserViewModel = viewModel()

        LaunchedEffect(uid) {
            userViewModel.load(uid)
        }

        val user = userViewModel.user

        UserEditProfileScreen(
            initialName = user?.name ?: "",
            initialEmail = user?.email ?: "",
            initialPhone = user?.phone ?: "",
            // isLoading is also true while the profile loads, only show saving once it has loaded
            isSaving = userViewModel.isLoading && user != null,
            errorMessage = userViewModel.errorMessage,
            onSaveClick = { name, email, phone ->
                userViewModel.updateProfile(uid, name, email, phone, onSaved = {
                    navController.popBackStack()
                })
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}
