package com.turing.campsite.ui.screens.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.turing.campsite.ui.screens.shared.section6Uid
import com.turing.campsite.ui.viewmodel.AdminSection6ViewModel

/**
 * Connects each admin destination to account-scoped state.
 */
@Composable
private fun AdminDestination(
    destination: String,
    load: (AdminSection6ViewModel, String) -> Unit,
    content: @Composable (AdminSection6ViewModel?, String?, String?) -> Unit
) {
    val uid = section6Uid()
    if (uid == null) {
        content(null, "Sign in as an administrator.", null)
        return
    }
    val model: AdminSection6ViewModel = viewModel(key = "$destination:$uid")
    LaunchedEffect(model, uid) { load(model, uid) }

    val status = when {
        model.isLoading -> "Loading…"
        model.errorMessage != null -> "Could not complete the request. Reopen this screen to retry."
        else -> model.statusMessage
    }
    content(model.takeIf { it.isAuthorized }, status, uid)
}

@Composable
internal fun AdminNotificationAuditRoute(onBackClick: () -> Unit) {
    AdminDestination("notifications", { model, uid -> model.loadNotifications(uid) }) { model, status, _ ->
        AdminNotificationLogScreen(onBackClick, model?.notifications.orEmpty(), status)
    }
}

@Composable
internal fun AdminMessageAuditRoute(onBackClick: () -> Unit, onConversationClick: (String) -> Unit) {
    AdminDestination("messages", { model, uid -> model.loadMessages(uid) }) { model, status, _ ->
        AdminMessageLogScreen(onBackClick, onConversationClick, model?.messages.orEmpty(), status)
    }
}

@Composable
internal fun AdminMessageConversationRoute(reservationId: String, onBackClick: () -> Unit) {
    AdminDestination(
        destination = "conversation:$reservationId",
        load = { model, uid -> model.loadConversation(uid, reservationId) }
    ) { model, status, _ ->
        AdminConversationHistoryScreen(model?.messages.orEmpty(), onBackClick, status)
    }
}

@Composable
internal fun AdminOwnerModerationRoute(onBackClick: () -> Unit) {
    AdminDestination("owner-moderation", { model, uid -> model.loadProfiles(uid) }) { model, status, uid ->
        AdminBrowseProfilesScreen(
            onBackClick = onBackClick,
            profiles = model?.users.orEmpty(),
            ownerRemovalEnabled = model != null && !model.isLoading,
            statusMessage = status,
            onRemoveOwner = { profile ->
                if (uid != null) model?.removeOwner(uid, profile)
            }
        )
    }
}
