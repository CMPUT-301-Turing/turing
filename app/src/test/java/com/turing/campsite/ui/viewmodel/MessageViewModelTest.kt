package com.turing.campsite.ui.viewmodel

import com.turing.campsite.data.model.Message
import com.turing.campsite.data.repository.MessageRepository
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
class MessageViewModelTest {

    private lateinit var repository: MessageRepository
    private lateinit var viewModel: MessageViewModel
    private lateinit var question: Message
    private lateinit var answer: Message

    // Set up a fake repository and a two message chat before every single test
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher()) // run background calls right away
        repository = mock(MessageRepository::class.java)
        viewModel = MessageViewModel(repository)
        question = Message(id = "m1", reservationId = "r1", fromUserId = "u1", body = "Is there parking?")
        answer = Message(id = "m2", reservationId = "r1", fromUserId = "owner1", body = "Yes, two spots.")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadForReservation_twoMessages_fillsList() = runBlocking<Unit> {
        doReturn(listOf(question, answer)).`when`(repository).listForReservation("r1")

        viewModel.loadForReservation("r1")

        assertEquals(listOf(question, answer), viewModel.messages)
    }

    @Test
    fun loadForReservation_repositoryFails_setsErrorMessage() = runBlocking<Unit> {
        doThrow(IllegalStateException("no internet")).`when`(repository).listForReservation("r1")

        viewModel.loadForReservation("r1")

        assertEquals("no internet", viewModel.errorMessage)
        assertTrue(viewModel.messages.isEmpty())
    }

    @Test
    fun loadAll_twoMessages_fillsList() = runBlocking<Unit> {
        doReturn(listOf(answer, question)).`when`(repository).listAll()

        viewModel.loadAll()

        assertEquals(2, viewModel.messages.size)
    }

    @Test
    fun send_newMessage_addedToEndOfChat() = runBlocking<Unit> {
        doReturn(listOf(question)).`when`(repository).listForReservation("r1")
        doReturn("m2").`when`(repository).add(answer)
        viewModel.loadForReservation("r1")

        viewModel.send(answer)

        verify(repository).add(answer)
        assertEquals(2, viewModel.messages.size) // 1 loaded + 1 sent
        assertEquals(answer, viewModel.messages.last())
    }
}
