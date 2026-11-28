/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Role-aware view switching using the signed-in profile and shared navigation guards.
 */
package com.turing.campsite.ui.screens.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.turing.campsite.data.auth.LocalCurrentUser
import com.turing.campsite.data.model.UserRole
import com.turing.campsite.navigation.Routes
import com.turing.campsite.navigation.switchView

/** Select a view without changing accounts or granting any roles. */
@Composable
fun LoginScreen(navController: NavHostController) {
    val user = LocalCurrentUser.current
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Choose a view")
        if (user?.hasRole(UserRole.ADMIN) == true) {
            Button(onClick = { navController.switchView(Routes.ADMIN_HOME) }) {
                Text("Admin view")
            }
        }
        if (user?.hasRole(UserRole.ENTRANT) == true) {
            Button(onClick = { navController.switchView(Routes.ENTRANT_HOME) }) {
                Text("Entrant view")
            }
        }
        if (user?.hasRole(UserRole.OWNER) == true) {
            Button(onClick = { navController.switchView(Routes.OWNER_HOME) }) {
                Text("Owner view")
            }
        }
        if (user != null && user.roles.isEmpty()) {
            Text("No views are enabled for your account.")
        }
    }
}
