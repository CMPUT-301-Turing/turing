package com.turing.campsite.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.junit.After
import org.junit.Before
import org.mockito.MockedStatic
import org.mockito.Mockito.*

/**
 * Creates an already-completed successful Task mock for the coroutine await fast path.
 *
 * @param value result exposed by the mock, including null for successful void writes.
 * @return a successful Task mock that does not schedule Android main-looper callbacks.
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> completed(value: T): Task<T> = (mock(Task::class.java) as Task<T>).also {
    doReturn(true).`when`(it).isComplete
    doReturn(false).`when`(it).isCanceled
    doReturn(null).`when`(it).exception
    doReturn(value).`when`(it).result
}
/**
 * Creates an already-completed failed Task mock for exception-propagation tests.
 *
 * @param error the exception exposed by the Task.
 * @return a failed Task mock; no Android callback executor is used.
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> failed(error: Exception): Task<T> = (mock(Task::class.java) as Task<T>).also {
    doReturn(true).`when`(it).isComplete
    doReturn(false).`when`(it).isCanceled
    doReturn(error).`when`(it).exception
}

/** Registers Mockito's any-object matcher and supplies a non-null placeholder for SDK parameters. */
internal fun anyPayload(): Any = any<Any>() ?: Any()

/**
 * Provides isolated Firestore singleton, collection and document mocks for repository tests.
 *
 * Explicit document("id") and generated document() calls resolve to different mocks.
 * The fixture models SDK interactions only, not server persistence or authorization.
 * @param collectionName top-level collection requested by the repository under test.
 */
abstract class FirestoreTestFixture(private val collectionName: String) {
    protected lateinit var db: FirebaseFirestore
    protected lateinit var collection: CollectionReference
    protected lateinit var doc: DocumentReference
    protected lateinit var generatedDoc: DocumentReference
    private lateinit var singleton: MockedStatic<FirebaseFirestore>

    /**
     * Installs the singleton mock and common document ID, successful write and delete responses.
     */
    @Before fun prepareFirestore() {
        db = mock(FirebaseFirestore::class.java)
        collection = mock(CollectionReference::class.java)
        doc = mock(DocumentReference::class.java)
        generatedDoc = generatedDocument("generated")
        singleton = mockStatic(FirebaseFirestore::class.java)
        singleton.`when`<FirebaseFirestore> { FirebaseFirestore.getInstance() }.thenReturn(db)
        doReturn(collection).`when`(db).collection(collectionName)
        doReturn(doc).`when`(collection).document("id")
        doReturn(generatedDoc).`when`(collection).document()
        doReturn("id").`when`(doc).id
        doReturn(completed<Void?>(null)).`when`(doc).set(anyPayload())
        doReturn(completed<Void?>(null)).`when`(doc).delete()
    }

    /**
     * Closes the static Firestore mock after the test to avoid cross-test singleton leakage.
     */
    @After fun releaseFirestore() { if (::singleton.isInitialized) singleton.close() }

    /** Creates a separate auto-ID document with a successful write, suitable for fan-out tests. */
    protected fun generatedDocument(id: String): DocumentReference {
        val reference = mock(DocumentReference::class.java)
        doReturn(id).`when`(reference).id
        doReturn(completed<Void?>(null)).`when`(reference).set(anyPayload())
        return reference
    }

    /**
     * Creates a document snapshot with a chosen mapped value and canonical document ID.
     *
     * @param type model class accepted by the stubbed conversion.
     * @param value mapped object, or null to model an unmappable document.
     * @param id document ID exposed by the snapshot.
     * @return a snapshot mock with only the requested conversion and ID stubbed.
     */
    protected fun <T : Any> row(type: Class<T>, value: T?, id: String = "id"): DocumentSnapshot {
        val snapshot = mock(DocumentSnapshot::class.java)
        doReturn(value).`when`(snapshot).toObject(type)
        doReturn(id).`when`(snapshot).id
        return snapshot
    }

    /**
     * Creates a query snapshot exposing the supplied documents in order.
     *
     * @param rows documents returned by the mock query.
     * @return a query snapshot mock; no server query is executed.
     */
    protected fun snapshot(vararg rows: DocumentSnapshot): QuerySnapshot {
        val snapshot = mock(QuerySnapshot::class.java)
        doReturn(rows.toList()).`when`(snapshot).documents
        return snapshot
    }
}
