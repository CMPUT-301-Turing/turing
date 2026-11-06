package com.turing.campsite.ui.viewmodel

import com.turing.campsite.data.model.User
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private lateinit var repository: UserRepository
    private lateinit var viewModel: UserViewModel
    private lateinit var alice: User

    // Set up a fake repository and a fresh view model before every single test
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher()) // run background calls right away
        repository = mock(UserRepository::class.java)
        viewModel = UserViewModel(repository)
        alice = User(id = "u1", name = "Alice", email = "alice@example.com")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_userExists_setsUser() = runBlocking<Unit> {
        doReturn(alice).`when`(repository).get("u1")

        viewModel.load("u1")

        assertEquals(alice, viewModel.user)
        assertFalse(viewModel.isLoading) // loading is finished
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun load_userMissing_userIsNull() = runBlocking<Unit> {
        doReturn(null).`when`(repository).get("nobody")

        viewModel.load("nobody")

        assertNull(viewModel.user)
    }

    @Test
    fun load_repositoryFails_setsErrorMessage() = runBlocking<Unit> {
        doThrow(IllegalStateException("no internet")).`when`(repository).get("u1")

        viewModel.load("u1")

        assertEquals("no internet", viewModel.errorMessage)
        assertNull(viewModel.user) // nothing was loaded
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun clearError_afterFailure_messageIsNull() = runBlocking<Unit> {
        doThrow(IllegalStateException("no internet")).`when`(repository).get("u1")
        viewModel.load("u1")

        viewModel.clearError()

        assertNull(viewModel.errorMessage)
    }

    @Test
    fun save_newUser_savesAndSetsUser() = runBlocking<Unit> {
        viewModel.save(alice)

        verify(repository).save(alice)
        assertEquals(alice, viewModel.user)
    }

    @Test
    fun updateProfile_loadedUser_updatesFields() = runBlocking<Unit> {
        doReturn(alice).`when`(repository).get("u1")
        viewModel.load("u1")

        viewModel.updateProfile("u1", "Alice B", "ab@example.com", "780-555-0100")

        verify(repository).updateProfile("u1", "Alice B", "ab@example.com", "780-555-0100")
        assertEquals("Alice B", viewModel.user?.name)
        assertEquals("ab@example.com", viewModel.user?.email)
        assertEquals("780-555-0100", viewModel.user?.phone)
    }

    @Test
    fun setNotificationsEnabled_turnedOff_userOptedOut() = runBlocking<Unit> {
        doReturn(alice).`when`(repository).get("u1")
        viewModel.load("u1")

        viewModel.setNotificationsEnabled("u1", false)

        verify(repository).setNotificationsEnabled("u1", false)
        assertEquals(false, viewModel.user?.notificationsEnabled) // was true by default
    }

    @Test
    fun loadAll_twoUsers_fillsList() = runBlocking<Unit> {
        val bob = User(id = "u2", name = "Bob")
        doReturn(listOf(alice, bob)).`when`(repository).listAll()

        viewModel.loadAll()

        assertEquals(2, viewModel.users.size)
        assertTrue(viewModel.users.contains(bob))
    }

    @Test
    fun delete_userInList_removedFromList() = runBlocking<Unit> {
        val bob = User(id = "u2", name = "Bob")
        doReturn(listOf(alice, bob)).`when`(repository).listAll()
        viewModel.loadAll()

        viewModel.delete("u1")

        verify(repository).delete("u1")
        assertEquals(listOf(bob), viewModel.users) // only Bob is left
    }
}
