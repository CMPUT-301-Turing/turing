/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Role-aware view switching using the signed-in profile and shared navigation guards.
 */

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
import com.turing.campsite.data.auth.LocalCurrentUser
import com.turing.campsite.data.model.UserRole

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
    roleComposable(Routes.ENTRANT_PROFILE, UserRole.ENTRANT, navController) {
        val user = LocalCurrentUser.current
        val uid = LocalCurrentUid.current
        val userViewModel: UserViewModel = viewModel()
        val switchView: (() -> Unit)? = if (user?.hasRole(UserRole.OWNER) == true) {
            { navController.switchView(Routes.OWNER_HOME) }
        } else null
        val switchToAdmin: (() -> Unit)? = if (user?.hasRole(UserRole.ADMIN) == true) {
            { navController.switchView(Routes.ADMIN_HOME) }
        } else null

        UserProfileScreen(
            currentView = UserView.ENTRANT,
            user = user,
            onNotificationsChange = { enabled ->
                userViewModel.setNotificationsEnabled(uid, enabled)
            },
            onEditProfileClick = { navController.navigate(Routes.USER_EDIT_PROFILE) },
            onPolicyClick = { navController.navigate(Routes.APP_POLICY) },
            onSwitchViewClick = switchView,
            onBackClick = { navController.popBackStack() },
            onSwitchToAdminClick = switchToAdmin
        )
    }

    // User profile when accessed from Owner view
    roleComposable(Routes.OWNER_PROFILE, UserRole.OWNER, navController) {
        val user = LocalCurrentUser.current
        val uid = LocalCurrentUid.current
        val userViewModel: UserViewModel = viewModel()
        val switchView: (() -> Unit)? = if (user?.hasRole(UserRole.ENTRANT) == true) {
            { navController.switchView(Routes.ENTRANT_HOME) }
        } else null
        val switchToAdmin: (() -> Unit)? = if (user?.hasRole(UserRole.ADMIN) == true) {
            { navController.switchView(Routes.ADMIN_HOME) }
        } else null

        UserProfileScreen(
            currentView = UserView.OWNER,
            user = user,
            onNotificationsChange = { enabled ->
                userViewModel.setNotificationsEnabled(uid, enabled)
            },
            onEditProfileClick = { navController.navigate(Routes.USER_EDIT_PROFILE) },
            onPolicyClick = { navController.navigate(Routes.APP_POLICY) },
            onSwitchViewClick = switchView,
            onBackClick = { navController.popBackStack() },
            onSwitchToAdminClick = switchToAdmin
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
