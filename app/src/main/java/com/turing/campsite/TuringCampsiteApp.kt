package com.turing.campsite

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

import com.turing.campsite.data.auth.LocalCurrentUid
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

    LaunchedEffect(uid) { authViewModel.observeAccountStatus() }
    if (authViewModel.deletionStatus != null || authViewModel.accountStatusError != null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val removed = authViewModel.deletionStatus == "DELETED"
            Text(if (authViewModel.accountStatusError != null) "Could not check account status. Retry to continue."
                else if (removed) "Your profile has been removed. This account cannot be used again."
                else "Profile removal is in progress. Normal account activity is blocked.")
            if (authViewModel.isLoading) CircularProgressIndicator()
            (authViewModel.accountStatusError ?: authViewModel.errorMessage)?.let { Text(it) }
            if (!removed && !authViewModel.isLoading) {
                Button(onClick = {
                    if (authViewModel.accountStatusError != null) authViewModel.observeAccountStatus()
                    else authViewModel.deleteSelf()
                }) { Text(if (authViewModel.accountStatusError != null) "Check account status" else "Resume profile removal") }
            }
        }
        return
    }

    CompositionLocalProvider(LocalCurrentUid provides uid) {
        val navController = rememberNavController()

        AppScaffold(navController) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.LOGIN,
                modifier = Modifier.padding(innerPadding)
            ) {
                sharedGraph(navController, onDeleteSelf = { authViewModel.deleteSelf() })
                entrantGraph(navController)
                ownerGraph(navController)
                adminGraph(navController, onDeleteSelf = { authViewModel.deleteSelf() })
            }
        }
    }
}
