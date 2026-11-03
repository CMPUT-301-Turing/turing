package com.turing.campsite

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun TuringCampsiteApp() {
    val navController = rememberNavController()

    var loginState: LoginUiState by remember {
        mutableStateOf(
            LoginUiState.Error("Unable to identify device")
        )
    }

    NavHost(
        navController = navController,
        startDestination = "Login"
    ) {
        composable("Login") {LoginScreen(
            navController = navController,
            loginState = loginState,
            onRetry = {
                loginState = LoginUiState.Loading
            })}
        composable("Register") {RegisterScreen(navController)}
        composable("LogOut") {LogOutScreen(navController)}

        composable("AdminHome") {AdminHomeScreen(navController)}
        composable("AdminProfile") {AdminProfileScreen(navController)}
        composable("AdminProfileSetting") {AdminProfileSettingScreen(navController)}
        composable("AdminCampsite") {AdminCampsiteScreen(navController)}
        composable("AdminMessageLog") {AdminMessageLogScreen(navController)}
        composable("AdminBrowseImage") {AdminBrowseImageScreen(navController)}
        composable("AdminNotificationLog") {AdminNotificationLogScreen(navController)}
        composable("AdminConversation") {AdminConversationScreen(navController)}

        composable("UserHome") {UserHomeScreen(navController)}//also the campsite screen
        composable("UserProfile") {UserProfileScreen(navController)}
        composable("UserEditProfile") {UserEditProfileScreen(navController)}
        composable("UserCampsiteList") {UserCampsiteListScreen(navController)}//clipboard icon
        composable("UserSelectCampsite") {UserSelectCampsiteScreen(navController)}
        composable("UserQRCode") {UserQRCodeScreen(navController)}
        composable("UserNotification") {UserNotificationScreen(navController)}
        composable("UserAcceptReservation") {UserAcceptReservationScreen(navController)}

        composable("OwnerHome") {OwnerHomeScreen(navController)}
        composable("OwnerRegisterNewCampsite") {OwnerRegisterNewCampsiteScreen(navController)}
        composable("OwnerCampsiteFullDetail") {OwnerCampsiteFullDetailScreen(navController)}
        composable("OwnerCampsiteHistory") {OwnerCampsiteHistoryScreen(navController)}//the one you can swap between 3 tabs active, past,cancelled
        composable("OwnerMessage") {OwnerMessageScreen(navController)}
        composable("OwnerLotteryDetails") {OwnerLotteryDetailsScreen(navController)}
        composable("OwnerEditLottery") {OwnerEditLotteryScreen(navController)}
        composable("OwnerCreateLottery") {OwnerCreateLotteryScreen(navController)}
        composable("OwnerCampsiteQRCode") {OwnerCampsiteQRCodeScreen(navController)}
        composable("OwnerEditCampsiteScreen") {OwnerEditCampsiteScreen(navController)}
        composable("OwnerNotification") {OwnerNotificationScreen(navController)}






    }
}

