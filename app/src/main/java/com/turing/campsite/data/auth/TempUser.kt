package com.turing.campsite.data.auth

/**
 * Temporary stand-in for the signed-in user.
 *
 * Sign-in on launch is not connected yet, so screens use this fixed uid when
 * they need to know who the current user is. Create a document with this id
 * in the "users" collection to test with it.
 *
 * TODO: delete this file once DeviceAuthManager.signIn() runs on launch and
 * use DeviceAuthManager.currentUid instead.
 *
 * @property UID the uid every screen should use until sign-in is done
 */
object TempUser {
    const val UID = "test-user-1"
}
