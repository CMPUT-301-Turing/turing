/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Role-aware view switching using the signed-in profile and shared navigation guards.
 */
package com.turing.campsite

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

import com.turing.campsite.data.auth.LocalCurrentUid
import com.turing.campsite.data.auth.LocalCurrentUser
import com.turing.campsite.navigation.Routes
import com.turing.campsite.navigation.AppScaffold
import com.turing.campsite.navigation.entrantGraph
import com.turing.campsite.navigation.ownerGraph
import com.turing.campsite.navigation.adminGraph
import com.turing.campsite.navigation.sharedGraph
import com.turing.campsite.ui.screens.shared.SignInScreen
import com.turing.campsite.ui.viewmodel.AuthViewModel

/**
 * Root of the Turing Campsite application.
 *
 * Signs the phone in first. Until that has finished, only the sign-in screen
 * is shown. After that, the navigation host is shown and every screen can
 * read the signed-in uid from LocalCurrentUid.
 *
 * @param authViewModel signs the phone in and holds its uid
 */
@Composable
fun TuringCampsiteApp(
    authViewModel: AuthViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        authViewModel.signIn()
    }

    val uid = authViewModel.uid
    if (uid == null) {
        SignInScreen(
            errorMessage = authViewModel.errorMessage,
            onRetryClick = { authViewModel.signIn() }
        )
        return
    }

    LaunchedEffect(uid) {
        authViewModel.observeCurrentUser()
    }

    val currentUser = authViewModel.currentUser
    if (authViewModel.isProfileLoading || currentUser == null) {
        SignInScreen(
            errorMessage = authViewModel.profileErrorMessage,
            onRetryClick = { authViewModel.observeCurrentUser() },
            loadingMessage = "Loading your profile...",
            errorTitle = "Could not load your profile"
        )
        return
    }

    CompositionLocalProvider(
        LocalCurrentUid provides uid,
        LocalCurrentUser provides currentUser
    ) {
        val navController = rememberNavController()

        AppScaffold(navController) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.LOGIN,
                modifier = Modifier.padding(innerPadding)
            ) {
                sharedGraph(navController, authViewModel)
                entrantGraph(navController)
                ownerGraph(navController)
                adminGraph(navController)
            }
        }
    }
}
