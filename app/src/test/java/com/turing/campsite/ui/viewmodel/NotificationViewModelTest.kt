package com.turing.campsite.ui.viewmodel

import com.turing.campsite.data.model.Notification
import com.turing.campsite.data.model.NotificationType
import com.turing.campsite.data.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    private lateinit var repository: NotificationRepository
    private lateinit var viewModel: NotificationViewModel
    private lateinit var won: Notification
    private lateinit var message: Notification

    // Set up a fake repository and two unread notifications before every single test
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher()) // run background calls right away
        repository = mock(NotificationRepository::class.java)
        viewModel = NotificationViewModel(repository)
        won = Notification(id = "n1", toUserId = "u1", type = NotificationType.LOTTERY_WON)
        message = Notification(id = "n2", toUserId = "u1", fromUserId = "owner1")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadForUser_twoNotifications_fillsList() = runBlocking<Unit> {
        doReturn(listOf(won, message)).`when`(repository).listForUser("u1")

        viewModel.loadForUser("u1")

        assertEquals(listOf(won, message), viewModel.notifications)
        assertEquals(2, viewModel.unreadCount) // both start unread
    }

    @Test
    fun loadForUser_repositoryFails_setsErrorMessage() = runBlocking<Unit> {
        doThrow(IllegalStateException("no internet")).`when`(repository).listForUser("u1")

        viewModel.loadForUser("u1")

        assertEquals("no internet", viewModel.errorMessage)
        assertTrue(viewModel.notifications.isEmpty())
    }

    @Test
    fun loadSentBy_ownerSentOne_fillsList() = runBlocking<Unit> {
        doReturn(listOf(message)).`when`(repository).listSentBy("owner1")

        viewModel.loadSentBy("owner1")

        assertEquals(listOf(message), viewModel.notifications)
    }

    @Test
    fun loadAll_twoNotifications_fillsList() = runBlocking<Unit> {
        doReturn(listOf(won, message)).`when`(repository).listAll()

        viewModel.loadAll()

        assertEquals(2, viewModel.notifications.size)
    }

    @Test
    fun send_oneNotification_savedInRepository() = runBlocking<Unit> {
        doReturn("n2").`when`(repository).add(message)

        viewModel.send(message)

        verify(repository).add(message)
    }

    @Test
    fun sendToMany_twoUsers_passedToRepository() = runBlocking<Unit> {
        viewModel.sendToMany(listOf("u1", "u2"), message)

        verify(repository).sendToMany(listOf("u1", "u2"), message)
    }

    @Test
    fun markRead_unreadNotification_unreadCountDrops() = runBlocking<Unit> {
        doReturn(listOf(won, message)).`when`(repository).listForUser("u1")
        viewModel.loadForUser("u1")

        viewModel.markRead("n1")

        verify(repository).markRead("n1")
        assertTrue(viewModel.notifications[0].read)
        assertEquals(1, viewModel.unreadCount) // 2 unread - 1 = 1
    }
}
