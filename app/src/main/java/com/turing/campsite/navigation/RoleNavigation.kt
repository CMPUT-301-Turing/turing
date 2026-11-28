/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Shared role checks for admin, owner and entrant navigation destinations.
 */
package com.turing.campsite.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.turing.campsite.data.auth.LocalCurrentUser
import com.turing.campsite.data.model.UserRole

/** Check the role before composing a destination or starting any of its data loads. */
fun NavGraphBuilder.roleComposable(
    route: String,
    requiredRole: UserRole,
    navController: NavHostController,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(route) { entry ->
        if (LocalCurrentUser.current?.hasRole(requiredRole) == true) {
            content(entry)
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("This view is not available for your account.")
                Button(onClick = { navController.switchView(Routes.LOGIN) }) {
                    Text("Choose a view")
                }
            }
        }
    }
}
