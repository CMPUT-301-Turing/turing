package com.turing.campsite.ui.viewmodel

import com.turing.campsite.data.model.Lottery
import com.turing.campsite.data.model.LotteryEntry
import com.turing.campsite.data.model.LotteryStatus
import com.turing.campsite.data.repository.LotteryRepository
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
class LotteryViewModelTest {

    private lateinit var repository: LotteryRepository
    private lateinit var viewModel: LotteryViewModel
    private lateinit var lottery: Lottery
    private lateinit var entry: LotteryEntry

    // Set up a fake repository, one open lottery and one entry before every single test
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher()) // run background calls right away
        repository = mock(LotteryRepository::class.java)
        viewModel = LotteryViewModel(repository)
        lottery = Lottery(id = "l1", ownerId = "owner1", winnerCount = 2)
        entry = LotteryEntry(id = "u1", entrantId = "u1")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadOpen_oneLottery_fillsList() = runBlocking<Unit> {
        doReturn(listOf(lottery)).`when`(repository).listOpen()

        viewModel.loadOpen()

        assertEquals(listOf(lottery), viewModel.lotteries)
    }

    @Test
    fun loadOpen_repositoryFails_setsErrorMessage() = runBlocking<Unit> {
        doThrow(IllegalStateException("no internet")).`when`(repository).listOpen()

        viewModel.loadOpen()

        assertEquals("no internet", viewModel.errorMessage)
        assertTrue(viewModel.lotteries.isEmpty())
    }

    @Test
    fun loadForOwner_ownerHasOne_fillsList() = runBlocking<Unit> {
        doReturn(listOf(lottery)).`when`(repository).listByOwner("owner1")

        viewModel.loadForOwner("owner1")

        assertEquals(listOf(lottery), viewModel.lotteries)
    }

    @Test
    fun select_entrantOnWaitlist_hasJoinedTrue() = runBlocking<Unit> {
        doReturn(lottery).`when`(repository).get("l1")
        doReturn(listOf(entry)).`when`(repository).listEntries("l1")

        viewModel.select("l1", "u1")

        assertEquals(lottery, viewModel.selectedLottery)
        assertEquals(1, viewModel.entries.size)
        assertTrue(viewModel.hasJoined) // u1 is on the waitlist
    }

    @Test
    fun select_entrantNotOnWaitlist_hasJoinedFalse() = runBlocking<Unit> {
        doReturn(lottery).`when`(repository).get("l1")
        doReturn(listOf(entry)).`when`(repository).listEntries("l1")

        viewModel.select("l1", "someoneElse")

        assertFalse(viewModel.hasJoined)
    }

    @Test
    fun select_lotteryMissing_setsErrorMessage() = runBlocking<Unit> {
        doReturn(null).`when`(repository).get("gone")

        viewModel.select("gone", "u1")

        assertNull(viewModel.selectedLottery)
        assertEquals("Lottery not found", viewModel.errorMessage)
    }

    @Test
    fun add_newLottery_savedAndAddedToList() = runBlocking<Unit> {
        doReturn("l1").`when`(repository).add(lottery)

        viewModel.add(lottery)

        verify(repository).add(lottery)
        assertEquals(listOf(lottery), viewModel.lotteries)
    }

    @Test
    fun update_lotteryInList_replacesOldCopy() = runBlocking<Unit> {
        doReturn(listOf(lottery)).`when`(repository).listOpen()
        viewModel.loadOpen()
        val edited = lottery.copy(winnerCount = 5)

        viewModel.update(edited)

        verify(repository).update(edited)
        assertEquals(5, viewModel.lotteries[0].winnerCount) // was 2
    }

    @Test
    fun setStatus_closed_updatesList() = runBlocking<Unit> {
        doReturn(listOf(lottery)).`when`(repository).listOpen()
        viewModel.loadOpen()

        viewModel.setStatus("l1", LotteryStatus.CLOSED)

        verify(repository).setStatus("l1", LotteryStatus.CLOSED)
        assertEquals(LotteryStatus.CLOSED, viewModel.lotteries[0].status)
    }

    @Test
    fun join_waitlistHasRoom_successful() = runBlocking<Unit> {
        doReturn(true).`when`(repository).join("l1", "u1")
        doReturn(listOf(entry)).`when`(repository).listEntries("l1")

        viewModel.join("l1", "u1")

        assertTrue(viewModel.hasJoined)
        assertEquals(1, viewModel.entries.size)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun join_waitlistFull_unsuccessful() = runBlocking<Unit> {
        doReturn(false).`when`(repository).join("l1", "u1")

        viewModel.join("l1", "u1")

        assertFalse(viewModel.hasJoined)
        assertEquals("Could not join, the waitlist is full", viewModel.errorMessage)
    }

    @Test
    fun leave_entrantOnWaitlist_removedFromEntries() = runBlocking<Unit> {
        doReturn(lottery).`when`(repository).get("l1")
        doReturn(listOf(entry)).`when`(repository).listEntries("l1")
        viewModel.select("l1", "u1")

        viewModel.leave("l1", "u1")

        verify(repository).leave("l1", "u1")
        assertFalse(viewModel.hasJoined)
        assertTrue(viewModel.entries.isEmpty()) // u1 was the only entry
    }
}
