package com.turing.campsite.ui.viewmodel

import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.ReservationStatus
import com.turing.campsite.data.repository.ReservationRepository
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
class ReservationViewModelTest {

    private lateinit var repository: ReservationRepository
    private lateinit var viewModel: ReservationViewModel
    private lateinit var first: Reservation
    private lateinit var second: Reservation

    // Set up a fake repository and two active reservations before every single test
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher()) // run background calls right away
        repository = mock(ReservationRepository::class.java)
        viewModel = ReservationViewModel(repository)
        first = Reservation(id = "r1", entrantId = "u1", ownerId = "owner1", campsiteId = "c1")
        second = Reservation(id = "r2", entrantId = "u1", ownerId = "owner1", campsiteId = "c2")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadForEntrant_twoReservations_fillsList() = runBlocking<Unit> {
        doReturn(listOf(first, second)).`when`(repository).listByEntrant("u1")

        viewModel.loadForEntrant("u1")

        assertEquals(listOf(first, second), viewModel.reservations)
    }

    @Test
    fun loadForEntrant_repositoryFails_setsErrorMessage() = runBlocking<Unit> {
        doThrow(IllegalStateException("no internet")).`when`(repository).listByEntrant("u1")

        viewModel.loadForEntrant("u1")

        assertEquals("no internet", viewModel.errorMessage)
        assertTrue(viewModel.reservations.isEmpty())
    }

    @Test
    fun loadForOwner_twoReservations_fillsList() = runBlocking<Unit> {
        doReturn(listOf(first, second)).`when`(repository).listByOwner("owner1")

        viewModel.loadForOwner("owner1")

        assertEquals(2, viewModel.reservations.size)
    }

    @Test
    fun loadActiveForCampsite_oneActive_fillsList() = runBlocking<Unit> {
        doReturn(listOf(first)).`when`(repository).listActiveForCampsite("c1")

        viewModel.loadActiveForCampsite("c1")

        assertEquals(listOf(first), viewModel.reservations)
    }

    @Test
    fun add_newReservation_savedAndAddedToList() = runBlocking<Unit> {
        doReturn("r1").`when`(repository).add(first)

        viewModel.add(first)

        verify(repository).add(first)
        assertEquals(listOf(first), viewModel.reservations)
    }

    @Test
    fun cancel_activeReservation_statusCancelled() = runBlocking<Unit> {
        doReturn(listOf(first, second)).`when`(repository).listByEntrant("u1")
        viewModel.loadForEntrant("u1")

        viewModel.cancel("r1")

        verify(repository).setStatus("r1", ReservationStatus.CANCELLED)
        assertEquals(ReservationStatus.CANCELLED, viewModel.reservations[0].status)
        assertEquals(ReservationStatus.ACTIVE, viewModel.reservations[1].status) // r2 untouched
    }

    @Test
    fun reject_activeReservation_statusRejected() = runBlocking<Unit> {
        doReturn(listOf(first, second)).`when`(repository).listByOwner("owner1")
        viewModel.loadForOwner("owner1")

        viewModel.reject("r2")

        verify(repository).setStatus("r2", ReservationStatus.REJECTED)
        assertEquals(ReservationStatus.REJECTED, viewModel.reservations[1].status)
        assertEquals(ReservationStatus.ACTIVE, viewModel.reservations[0].status) // r1 untouched
    }
}
