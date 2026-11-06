package com.turing.campsite.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.viewmodel.BaseViewModel

/**
 * Shows the error of a view model as a short message at the bottom of the screen.
 *
 * The error is cleared after it is shown so the same message can appear again
 * if the call fails a second time.
 *
 * @param viewModel the view model whose errorMessage is watched
 */
@Composable
fun ErrorToast(viewModel: BaseViewModel) {
    val context = LocalContext.current
    val message = viewModel.errorMessage
    LaunchedEffect(message) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
}

/**
 * A spinner in the middle of the screen, shown while one item is loading.
 */
@Composable
fun LoadingBox() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryColor)
    }
}
