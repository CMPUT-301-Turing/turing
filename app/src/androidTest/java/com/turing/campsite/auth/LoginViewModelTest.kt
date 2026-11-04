package com.turing.campsite.auth

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.app.Application
import com.turing.campsite.ui.screens.shared.LoginUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginViewModelTest {

    private fun createViewModel(): LoginViewModel {
        val application =
            ApplicationProvider.getApplicationContext<Application>()

        return LoginViewModel(application)
    }

    @Test
    fun validDeviceIdProducesReady() {
        val viewModel = createViewModel()

        viewModel.setDeviceIdProviderForTesting(
            object : DeviceIdProvider {
                override fun getDeviceId() = "test-device-123"
            }
        )

        viewModel.identifyDevice()

        assertEquals(
            LoginUiState.Ready,
            viewModel.loginState
        )
    }

    @Test
    fun emptyDeviceIdProducesError() {
        val viewModel = createViewModel()

        viewModel.setDeviceIdProviderForTesting(
            object : DeviceIdProvider {
                override fun getDeviceId() = ""
            }
        )

        viewModel.identifyDevice()

        assertTrue(
            viewModel.loginState is LoginUiState.Error
        )
    }

    @Test
    fun providerExceptionProducesError() {
        val viewModel = createViewModel()

        viewModel.setDeviceIdProviderForTesting(
            object : DeviceIdProvider {
                override fun getDeviceId(): String {
                    throw IllegalStateException("Simulated failure")
                }
            }
        )

        viewModel.identifyDevice()

        assertEquals(
            LoginUiState.Error("Unable to identify device"),
            viewModel.loginState
        )
    }

    @Test
    fun retryAfterFailureProducesReady() {
        val viewModel = createViewModel()

        var shouldFail = true

        viewModel.setDeviceIdProviderForTesting(
            object : DeviceIdProvider {
                override fun getDeviceId(): String {
                    if (shouldFail) {
                        throw IllegalStateException("Simulated failure")
                    }
                    return "test-device-123"
                }
            }
        )

        viewModel.identifyDevice()

        assertTrue(
            viewModel.loginState is LoginUiState.Error
        )

        shouldFail = false

        viewModel.identifyDevice()

        assertEquals(
            LoginUiState.Ready,
            viewModel.loginState
        )
    }
}