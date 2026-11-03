package com.turing.campsite.auth

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeviceAuthManagerTest {

    @Test
    fun deviceIdIsStableAndNonBlank() {
        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        val manager = DeviceAuthManager(context)

        val firstId = manager.getDeviceId()
        val secondId = manager.getDeviceId()

        assertTrue(firstId.isNotBlank())
        assertEquals(firstId, secondId)
    }
}