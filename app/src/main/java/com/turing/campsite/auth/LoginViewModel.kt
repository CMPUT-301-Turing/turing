package com.turing.campsite.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.ui.screens.shared.LoginUiState

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var deviceIdProvider: DeviceIdProvider =
        DeviceAuthManager(application.applicationContext)

    var loginState: LoginUiState by mutableStateOf(
        LoginUiState.Loading
    )
        private set

    init {
        identifyDevice()
    }

    fun identifyDevice() {
        loginState = LoginUiState.Loading

        loginState = try {
            val deviceId = deviceIdProvider.getDeviceId()

            if (deviceId.isBlank()) {
                LoginUiState.Error("Device identifier is empty")
            } else {
                LoginUiState.Ready
            }
        } catch (exception: Exception) {
            LoginUiState.Error(
                "Unable to identify device"
            )
        }
    }

    internal fun setDeviceIdProviderForTesting(
        provider: DeviceIdProvider
    ) {
        deviceIdProvider = provider
    }
}