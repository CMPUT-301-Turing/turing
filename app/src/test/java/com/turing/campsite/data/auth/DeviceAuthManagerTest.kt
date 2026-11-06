package com.turing.campsite.data.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.turing.campsite.data.repository.completed
import com.turing.campsite.data.repository.failed
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.MockedStatic
import org.mockito.Mockito.*

/**
 * Tests [DeviceAuthManager] against mocked Firebase boundaries.
 *
 * Covers local mapping, branching and error behavior; does not verify live persistence or security rules.
 */
class DeviceAuthManagerTest {
    private lateinit var singleton: MockedStatic<FirebaseAuth>
    private lateinit var auth: FirebaseAuth
    private lateinit var manager: DeviceAuthManager

    /** Installs the auth singleton mock before constructing the manager. */
    @Before fun setUp() {
        auth = mock(FirebaseAuth::class.java)
        singleton = mockStatic(FirebaseAuth::class.java)
        singleton.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(auth)
        manager = DeviceAuthManager()
    }
    /** Closes the static auth mock so subsequent tests do not inherit it. */
    @After fun tearDown() { if (::singleton.isInitialized) singleton.close() }

    /** Creates a Firebase user mock that returns the supplied UID. */
    private fun user(uid: String): FirebaseUser = mock(FirebaseUser::class.java).also {
        doReturn(uid).`when`(it).uid
    }

    /**
     * Given no current Firebase user, expects a null current UID.
     */
    @Test fun currentUidIsNullBeforeSignIn() {
        assertNull(manager.currentUid)
    }
    /**
     * Given changing current users and then no user, expects each UID lookup to reflect the latest auth state.
     */
    @Test fun currentUidReadsTheCurrentAuthState() {
        doReturn(user("first")).`when`(auth).currentUser
        assertEquals("first", manager.currentUid)
        doReturn(user("second")).`when`(auth).currentUser
        assertEquals("second", manager.currentUid)
        doReturn(null).`when`(auth).currentUser
        assertNull(manager.currentUid)
    }
    /**
     * Given an existing user, expects repeated sign-ins to return that UID without calling anonymous sign-in.
     */
    @Test fun signInReusesExistingIdentityWithoutNetworkCall() = runBlocking<Unit> {
        doReturn(user("existing")).`when`(auth).currentUser
        assertEquals("existing", manager.signIn())
        assertEquals("existing", manager.signIn())
        verify(auth, never()).signInAnonymously()
    }
    /**
     * Given no current user and a successful anonymous-auth result, expects the new user UID.
     */
    @Test fun signInReturnsAnonymousResultUid() = runBlocking<Unit> {
        val result = mock(AuthResult::class.java)
        doReturn(user("new")).`when`(result).user
        doReturn(completed(result)).`when`(auth).signInAnonymously()
        assertEquals("new", manager.signIn())
        verify(auth).signInAnonymously()
    }
    /**
     * Given failed anonymous authentication, expects the same exception to propagate.
     */
    @Test fun signInPropagatesAuthFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("authentication failed")
        doReturn(failed<AuthResult>(failure)).`when`(auth).signInAnonymously()
        try { manager.signIn(); fail("Expected auth failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }
    /**
     * Characterizes a successful auth result with no user: the current implementation throws a null-pointer exception.
     */
    @Test fun missingUserInSuccessfulAuthResultCurrentlyThrows() = runBlocking<Unit> {
        val result = mock(AuthResult::class.java)
        doReturn(completed(result)).`when`(auth).signInAnonymously()
        try { manager.signIn(); fail("Expected null-user failure") }
        catch (_: NullPointerException) { /* Characterizes the production !!, not desired UX. */ }
    }
    /**
     * Expects sign-out to delegate to the mocked Firebase Auth instance.
     */
    @Test fun signOutDelegatesToFirebase() {
        manager.signOut()
        verify(auth).signOut()
    }
    /**
     * Given a sign-out exception, expects the same exception to propagate.
     */
    @Test fun signOutPropagatesFailure() {
        val failure = IllegalStateException("sign-out failed")
        doThrow(failure).`when`(auth).signOut()
        try { manager.signOut(); fail("Expected sign-out failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }
}
