package com.turing.campsite.auth

import android.content.Context
import android.provider.Settings

class DeviceAuthManager(
    private val context: Context
) {
    fun getDeviceId(): String {
        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        return deviceId ?: throw IllegalStateException(
            "Unable to retrieve device identifier"
        )
    }
}