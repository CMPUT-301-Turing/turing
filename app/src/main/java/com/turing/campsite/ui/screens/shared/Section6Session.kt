package com.turing.campsite.ui.screens.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth

/**
 * Observes the current account rather than using the app's temporary fixture ID.
 */
@Composable
internal fun section6Uid(): String? {
    val auth = remember { FirebaseAuth.getInstance() }
    var uid by remember(auth) { mutableStateOf(auth.currentUser?.uid) }
    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { uid = it.currentUser?.uid }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }
    return uid
}
