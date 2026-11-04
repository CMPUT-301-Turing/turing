package com.turing.campsite.ui.screens.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.turing.campsite.navigation.Routes

sealed interface LoginUiState {
    data object Loading : LoginUiState
    data object Ready : LoginUiState
    data class Error(val message: String) : LoginUiState
}

@Composable
fun LoginScreen(
    navController: NavController,
    loginState: LoginUiState,
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Campsite",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = loginState) {
            LoginUiState.Loading -> {
                Text("Identifying your device...")

                Spacer(modifier = Modifier.height(24.dp))

                CircularProgressIndicator()
            }

            LoginUiState.Ready -> {
                Text("Device identified successfully")
            }

            is LoginUiState.Error -> {
                Text(state.message)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onRetry,
                    modifier = Modifier.testTag("retryLoginButton")
                ) {
                    Text("Retry")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Temporary navigation shortcuts for development only.
        Button(
            onClick = {
                navController.navigate("AdminHome")
            }
        ) {
            Text("Admin Screen")
        }

        Button(
            onClick = {
                navController.navigate(Routes.ENTRANT_HOME)
            }
        ) {
            Text("Entrant Screen")
        }

        Button(
            onClick = {
                navController.navigate(Routes.OWNER_HOME)
            }
        ) {
            Text("Owner Screen")
        }
    }
}
