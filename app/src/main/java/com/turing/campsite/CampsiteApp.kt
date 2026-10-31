package com.turing.campsite

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

/**
 * Root composable that owns the navigation graph.
 *
 * Starts on the `login` route and currently knows two destinations:
 * `login` ([com.turing.campsite.ui.LoginScreen]) and `AdminHome` ([com.turing.campsite.ui.home.AdminHomeScreen]).
 */
@Composable
fun CampsiteApp (){
    val navController = rememberNavController()


}
