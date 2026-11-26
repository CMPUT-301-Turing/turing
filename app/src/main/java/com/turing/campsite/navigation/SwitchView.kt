package com.turing.campsite.navigation

import androidx.navigation.NavHostController

/**
 * Switches to another view (entrant, owner or admin).
 *
 * Clears the screens of the old view so that pressing back from the new
 * view's home goes to the login screen instead of the old view.
 *
 * @param route the home route of the view to open
 */
fun NavHostController.switchView(route: String) {
    navigate(route) {
        popUpTo(Routes.LOGIN)
        launchSingleTop = true
    }
}
