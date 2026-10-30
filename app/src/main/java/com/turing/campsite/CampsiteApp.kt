package com.turing.campsite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun CampsiteApp (){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login"){
        composable("login") {LoginScreen(navController)}
        composable("AdminHome") {AdminHomeScreen(navController)}
    }

}
