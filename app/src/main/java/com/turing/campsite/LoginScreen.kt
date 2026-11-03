package com.turing.campsite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.testTag

sealed interface LoginUiState {
    data object Loading : LoginUiState
    data object Ready : LoginUiState
    data class Error(val message: String) : LoginUiState
}
@Composable
fun LoginScreen(
    navController: NavController,
    loginState: LoginUiState,
    onRetry: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text= "Campsite",
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
                    onClick = {
                        onRetry()
                    },
                    modifier = Modifier.testTag("retryLoginButton")
                ) {
                    Text("Retry")
                }
            }
        }

        Row(){
            Button(
                onClick={
                    navController.navigate("AdminHome")
                }
            ) {
                Text("admin screen")
            }

        }
    }

}