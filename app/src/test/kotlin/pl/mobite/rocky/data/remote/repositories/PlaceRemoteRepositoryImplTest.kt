package pl.mobite.rocky.data.remote.repositories

import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.remote.backend.MusicBrainzBackend
import pl.mobite.rocky.data.remote.backend.responses.PlaceBackendResponse
import pl.mobite.rocky.data.remote.backend.responses.PlacesBackendResponse
import pl.mobite.rocky.data.remote.repository.PlaceRemoteRepositoryImpl
import pl.mobite.rocky.data.repositories.models.Place
import pl.mobite.rocky.utils.lazyMock


class PlaceRemoteRepositoryImplTest {

    private val musicBrainzBackendMock: MusicBrainzBackend by lazyMock()

    private lateinit var remoteRepository: PlaceRemoteRepositoryImpl
    private lateinit var testObserver: TestObserver<List<Place>>

    private val pageLimit = 10

    @Before
    fun setUp() {
        remoteRepository = PlaceRemoteRepositoryImpl({ musicBrainzBackendMock }, pageLimit, 0)
        testObserver = TestObserver()
    }

    @Test
    fun testOnePageQuery() {
        val count = 7
        val placeApiResponseList = createDummyPlaceApiResponseList(count, pageLimit)
        val placeListExpected = placeApiResponseList.getAllPlaceApiAsList()
        placeApiResponseList.forEachIndexed { i, list ->
            `when`(musicBrainzBackendMock.getPlaces(dummyQuery.withYearFilter(), getPageOffset(i, pageLimit), pageLimit))
                    .thenReturn(Single.just(list))
        }

        remoteRepository.fetchAllPlacesFrom1990(dummyQuery)
                .subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValueAt(0, placeListExpected)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testMultiPageQuery() {
        val count = 65
        val placeApiResponseList = createDummyPlaceApiResponseList(count, pageLimit)
        val placeApiListExpected = placeApiResponseList.getAllPlaceApiAsList()
        placeApiResponseList.forEachIndexed { i, list ->
            `when`(musicBrainzBackendMock.getPlaces(dummyQuery.withYearFilter(), getPageOffset(i, pageLimit), pageLimit))
                    .thenReturn(Single.just(list))
        }

        /* I need to use blockingGet because testObserver wont work */
        val placesApiListTested = remoteRepository.fetchAllPlacesFrom1990(dummyQuery).blockingGet()

        assertEquals(placeApiListExpected, placesApiListTested)
    }

    @Test
    fun testOnePageQueryError() {
        `when`(musicBrainzBackendMock.getPlaces(dummyQuery.withYearFilter(), 0, pageLimit))
                .thenReturn(Single.error(dummyException))

        remoteRepository.fetchAllPlacesFrom1990(dummyQuery)
                .subscribe(testObserver)

        testObserver.assertNotComplete()
        testObserver.assertError(dummyException)
    }


    @Test
    fun testMultiPageQueryError() {
        val count = 65
        val placeApiResponseList = createDummyPlaceApiResponseList(count, pageLimit)
        placeApiResponseList.forEachIndexed { i, list ->
            val single = if (i < placeApiResponseList.size - 1) {
                Single.just(list)
            } else {
                /* throw error in last request */
                Single.error(dummyException)
            }
            `when`(musicBrainzBackendMock.getPlaces(dummyQuery.withYearFilter(), getPageOffset(i, pageLimit), pageLimit))
                    .thenReturn(single)
        }

        /* I need to use blockingGet because testObserver wont work,
         * and it causes that the exception needs to be catch in this way */
        try {
            remoteRepository.fetchAllPlacesFrom1990(dummyQuery).blockingGet()
            assertTrue("TextException should be thrown", false)
        } catch (e: Throwable) {
            assertTrue(e.cause is DummyException)
        }
    }

    companion object {

        private const val dummyQuery = "query"
        private val dummyException = DummyException()

        private fun String.withYearFilter() = "$this AND begin:[1990 TO 2200]"

        private class DummyException: Throwable("dummy exception")

        private fun createDummyPlaceApiResponseList(count: Int, queryLimit: Int): List<PlacesBackendResponse> {
            val pageNumbers = Math.ceil(count / queryLimit.toDouble()).toInt()
            return (0 until pageNumbers).map { pageNumber ->
                val firstId = pageNumber * queryLimit
                val placeApiCount = if (pageNumber < pageNumbers - 1) queryLimit else count - (pageNumber * queryLimit)
                PlacesBackendResponse(
                    null, count, getPageOffset(pageNumber, queryLimit), createDummyPlaceApiList(firstId, placeApiCount)
                )
            }
        }

        private fun createDummyPlaceApiList(firstId: Int, count: Int): List<PlaceBackendResponse> {
            return (0 until count).map { i -> createDummyPlaceAPI(firstId + i) }
        }

        private fun createDummyPlaceAPI(id: Int) = PlaceBackendResponse(
            id.toString(), "Studio", "23", id * 10, "Studio name $id", "Studio address $id", null, null, null
        )

        private fun getPageOffset(pageNumber: Int, queryLimit: Int) = pageNumber * queryLimit

        private fun List<PlacesBackendResponse>.getAllPlaceApiAsList(): List<PlaceBackendResponse> {
            val list = mutableListOf<PlaceBackendResponse>()
            forEach { placeAPiResponse ->
                placeAPiResponse.placeResponses?.let { list.addAll(it.filterNotNull()) }
            }
            return list
        }
    }
}