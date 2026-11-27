package com.turing.campsite.ui.viewmodel

import com.turing.campsite.data.auth.DeviceAuthManager
import com.turing.campsite.data.model.User
import com.turing.campsite.data.model.UserRole
import com.turing.campsite.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

/**
 * Tests [AuthViewModel] with a fake auth manager and user repository.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var authManager: DeviceAuthManager
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher()) // run background calls right away
        authManager = mock(DeviceAuthManager::class.java)
        userRepository = mock(UserRepository::class.java)
        viewModel = AuthViewModel(authManager, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uid_beforeSignIn_isNull() {
        assertNull(viewModel.uid)
    }

    @Test
    fun signIn_newPhone_createsUserDocument() = runBlocking<Unit> {
        doReturn("uid1").`when`(authManager).signIn()
        doReturn(null).`when`(userRepository).get("uid1")

        viewModel.signIn()

        assertEquals("uid1", viewModel.uid)
        val saved = savedUser()
        assertEquals("uid1", saved.id)
        assertEquals(listOf(UserRole.ENTRANT, UserRole.OWNER), saved.roles)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun signIn_existingUser_doesNotOverwriteUserDocument() = runBlocking<Unit> {
        doReturn("uid1").`when`(authManager).signIn()
        doReturn(User(id = "uid1", name = "Sam")).`when`(userRepository).get("uid1")

        viewModel.signIn()

        assertEquals("uid1", viewModel.uid)
        verify(userRepository, never()).save(any() ?: User())
    }

    @Test
    fun signIn_authFails_setsErrorAndKeepsUidNull() = runBlocking<Unit> {
        doThrow(IllegalStateException("no internet")).`when`(authManager).signIn()

        viewModel.signIn()

        assertNull(viewModel.uid)
        assertEquals("no internet", viewModel.errorMessage)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun signIn_retryAfterFailure_signsIn() = runBlocking<Unit> {
        doThrow(IllegalStateException("no internet")).`when`(authManager).signIn()
        viewModel.signIn()

        doReturn("uid1").`when`(authManager).signIn()
        doReturn(User(id = "uid1")).`when`(userRepository).get("uid1")
        viewModel.signIn()

        assertEquals("uid1", viewModel.uid)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun signIn_alreadySignedIn_doesNotSignInAgain() = runBlocking<Unit> {
        doReturn("uid1").`when`(authManager).signIn()
        doReturn(User(id = "uid1")).`when`(userRepository).get("uid1")

        viewModel.signIn()
        viewModel.signIn()

        verify(authManager, times(1)).signIn()
    }

    /** Returns the user that was passed to save(). */
    private suspend fun savedUser(): User {
        val captor = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(captor.capture() ?: User())
        return captor.value
    }
}
