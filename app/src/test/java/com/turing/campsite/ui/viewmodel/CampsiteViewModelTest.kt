package com.turing.campsite.ui.viewmodel

import com.turing.campsite.data.model.Campsite
import com.turing.campsite.data.model.Review
import com.turing.campsite.data.repository.CampsiteRepository
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
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

@OptIn(ExperimentalCoroutinesApi::class)
class CampsiteViewModelTest {

    private lateinit var repository: CampsiteRepository
    private lateinit var viewModel: CampsiteViewModel
    private lateinit var pinewood: Campsite
    private lateinit var lakeside: Campsite

    // Set up a fake repository and two campsites before every single test
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher()) // run background calls right away
        repository = mock(CampsiteRepository::class.java)
        viewModel = CampsiteViewModel(repository)
        pinewood = Campsite(id = "c1", ownerId = "owner1", name = "Pinewood")
        lakeside = Campsite(id = "c2", ownerId = "owner1", name = "Lakeside")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadAll_twoCampsites_fillsList() = runBlocking<Unit> {
        doReturn(listOf(pinewood, lakeside)).`when`(repository).listAll()

        viewModel.loadAll()

        assertEquals(listOf(pinewood, lakeside), viewModel.campsites)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun loadAll_repositoryFails_setsErrorMessage() = runBlocking<Unit> {
        doThrow(IllegalStateException("no internet")).`when`(repository).listAll()

        viewModel.loadAll()

        assertEquals("no internet", viewModel.errorMessage)
        assertTrue(viewModel.campsites.isEmpty()) // list is left as it was
    }

    @Test
    fun loadForOwner_ownerHasOne_fillsList() = runBlocking<Unit> {
        doReturn(listOf(pinewood)).`when`(repository).listByOwner("owner1")

        viewModel.loadForOwner("owner1")

        assertEquals(listOf(pinewood), viewModel.campsites)
    }

    @Test
    fun select_campsiteExists_setsCampsiteAndReviews() = runBlocking<Unit> {
        val review = Review(id = "r1", entrantId = "u1", rating = 5)
        doReturn(pinewood).`when`(repository).get("c1")
        doReturn(listOf(review)).`when`(repository).listReviews("c1")

        viewModel.select("c1")

        assertEquals(pinewood, viewModel.selectedCampsite)
        assertEquals(listOf(review), viewModel.reviews)
    }

    @Test
    fun select_campsiteMissing_setsErrorMessage() = runBlocking<Unit> {
        doReturn(null).`when`(repository).get("gone")

        viewModel.select("gone")

        assertNull(viewModel.selectedCampsite)
        assertEquals("Campsite not found", viewModel.errorMessage)
    }

    @Test
    fun add_newCampsite_savedAndAddedToList() = runBlocking<Unit> {
        doReturn("c1").`when`(repository).add(pinewood)

        viewModel.add(pinewood)

        verify(repository).add(pinewood)
        assertEquals(listOf(pinewood), viewModel.campsites)
    }

    @Test
    fun add_newCampsite_callsOnDoneAfterSaving() = runBlocking<Unit> {
        doReturn("c1").`when`(repository).add(pinewood)
        var done = false

        viewModel.add(pinewood) { done = true }

        assertTrue(done)
    }

    @Test
    fun add_blankName_notSavedAndSetsError() = runBlocking<Unit> {
        val unnamed = Campsite(ownerId = "owner-1", name = "  ")
        var done = false

        viewModel.add(unnamed) { done = true }

        verify(repository, never()).add(unnamed)
        assertEquals("Campsite needs a name", viewModel.errorMessage)
        assertTrue(viewModel.campsites.isEmpty())
        assertFalse(done) // onDone must not run when nothing was saved
    }

    @Test
    fun update_campsiteInList_replacesOldCopy() = runBlocking<Unit> {
        doReturn(listOf(pinewood, lakeside)).`when`(repository).listAll()
        viewModel.loadAll()
        val renamed = pinewood.copy(name = "Pinewood Retreat")

        viewModel.update(renamed)

        verify(repository).update(renamed)
        assertEquals("Pinewood Retreat", viewModel.campsites[0].name)
        assertEquals(2, viewModel.campsites.size) // nothing added or removed
    }

    @Test
    fun delete_campsiteInList_removedFromList() = runBlocking<Unit> {
        doReturn(listOf(pinewood, lakeside)).`when`(repository).listAll()
        viewModel.loadAll()

        viewModel.delete("c1")

        verify(repository).delete("c1")
        assertEquals(listOf(lakeside), viewModel.campsites) // only Lakeside is left
    }

    @Test
    fun addReview_selectedCampsite_reloadsRating() = runBlocking<Unit> {
        val review = Review(id = "r1", entrantId = "u1", rating = 4)
        val rated = pinewood.copy(ratingAverage = 4.0, ratingCount = 1)
        doReturn(rated).`when`(repository).get("c1")
        doReturn(listOf(review)).`when`(repository).listReviews("c1")

        viewModel.addReview("c1", review)

        verify(repository).addReview("c1", review)
        assertEquals(4.0, viewModel.selectedCampsite!!.ratingAverage, 0.001)
        assertEquals(1, viewModel.reviews.size)
    }
}
