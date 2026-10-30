package com.turing.campsite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController

/**
 * Root composable that owns the navigation graph.
 *
 * Starts on the `login` route and currently knows two destinations:
 * `login` ([LoginScreen]) and `AdminHome` ([AdminHomeScreen]).
 */
@Composable
fun CampsiteApp (){
    val navController = rememberNavController()


}
