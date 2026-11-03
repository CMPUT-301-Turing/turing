package com.turing.campsite.auth

interface DeviceIdProvider {
    fun getDeviceId(): String
}