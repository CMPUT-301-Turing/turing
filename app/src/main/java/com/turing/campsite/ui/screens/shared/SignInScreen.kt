/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Reuse loading/retry presentation for authenticated profile loading.
 */
package com.turing.campsite.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.BackgroundColor
import com.turing.campsite.PrimaryColor

/**
 * Shown while the phone is being signed in on launch.
 *
 * Shows a spinner while sign-in runs. If sign-in failed, shows the error
 * and a button to try again.
 *
 * @param errorMessage why sign-in failed, or null while it is still running
 * @param onRetryClick called when the user taps "Try again"
 */
@Composable
fun SignInScreen(
    errorMessage: String?,
    onRetryClick: () -> Unit,
    loadingMessage: String = "Signing in...",
    errorTitle: String = "Could not sign in"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (errorMessage == null) {
            CircularProgressIndicator(color = PrimaryColor)
            Text(
                text = loadingMessage,
                fontSize = 16.sp,
                color = PrimaryColor
            )
        } else {
            Text(
                text = errorTitle,
                fontSize = 18.sp,
                color = PrimaryColor
            )
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                color = PrimaryColor,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetryClick,
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Try again")
            }
        }
    }
}
