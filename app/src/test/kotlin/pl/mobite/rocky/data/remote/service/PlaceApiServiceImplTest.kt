package pl.mobite.rocky.data.remote.service

import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.remote.MusicBrainzService
import pl.mobite.rocky.data.remote.models.PlaceApi
import pl.mobite.rocky.data.remote.models.PlaceApiResponse
import pl.mobite.rocky.data.remote.services.PlaceApiServiceImpl
import pl.mobite.rocky.utils.lazyMock


class PlaceApiServiceImplTest {

    private val musicBrainzServiceMock: MusicBrainzService by lazyMock()

    private lateinit var placeApiServiceImpl: PlaceApiServiceImpl
    private lateinit var testObserver: TestObserver<List<PlaceApi>>

    private val pageLimit = 10

    @Before
    fun setUp() {
        placeApiServiceImpl = PlaceApiServiceImpl({ musicBrainzServiceMock }, pageLimit, 0)
        testObserver = TestObserver()
    }

    @Test
    fun testOnePageQuery() {
        val count = 7
        val placeApiResponseList = createDummyPlaceApiResponseList(count, pageLimit)
        val placeApiListExpected = placeApiResponseList.getAllPlaceApiAsList()
        placeApiResponseList.forEachIndexed { i, list ->
            `when`(musicBrainzServiceMock.getPlaces(dummyQuery.withYearFilter(), getPageOffset(i, pageLimit), pageLimit))
                    .thenReturn(Single.just(list))
        }

        placeApiServiceImpl.fetchAllPlacesFrom1990(dummyQuery)
                .subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValueAt(0, placeApiListExpected)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun testMultiPageQuery() {
        val count = 65
        val placeApiResponseList = createDummyPlaceApiResponseList(count, pageLimit)
        val placeApiListExpected = placeApiResponseList.getAllPlaceApiAsList()
        placeApiResponseList.forEachIndexed { i, list ->
            `when`(musicBrainzServiceMock.getPlaces(dummyQuery.withYearFilter(), getPageOffset(i, pageLimit), pageLimit))
                    .thenReturn(Single.just(list))
        }

        /* I need to use blockingGet because testObserver wont work */
        val placesApiListTested = placeApiServiceImpl.fetchAllPlacesFrom1990(dummyQuery).blockingGet()

        assertEquals(placeApiListExpected, placesApiListTested)
    }

    @Test
    fun testOnePageQueryError() {
        `when`(musicBrainzServiceMock.getPlaces(dummyQuery.withYearFilter(), 0, pageLimit))
                .thenReturn(Single.error(dummyException))

        placeApiServiceImpl.fetchAllPlacesFrom1990(dummyQuery)
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
            `when`(musicBrainzServiceMock.getPlaces(dummyQuery.withYearFilter(), getPageOffset(i, pageLimit), pageLimit))
                    .thenReturn(single)
        }

        /* I need to use blockingGet because testObserver wont work,
         * and it causes that the exception needs to be catch in this way */
        try {
            placeApiServiceImpl.fetchAllPlacesFrom1990(dummyQuery).blockingGet()
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

        private fun createDummyPlaceApiResponseList(count: Int, queryLimit: Int): List<PlaceApiResponse> {
            val pageNumbers = Math.ceil(count / queryLimit.toDouble()).toInt()
            return (0 until pageNumbers).map { pageNumber ->
                val firstId = pageNumber * queryLimit
                val placeApiCount = if (pageNumber < pageNumbers - 1) queryLimit else count - (pageNumber * queryLimit)
                PlaceApiResponse(null, count, getPageOffset(pageNumber, queryLimit), createDummyPlaceApiList(firstId, placeApiCount))
            }
        }

        private fun createDummyPlaceApiList(firstId: Int, count: Int): List<PlaceApi> {
            return (0 until count).map { i -> createDummyPlaceAPI(firstId + i) }
        }

        private fun createDummyPlaceAPI(id: Int) = PlaceApi(
                id.toString(),
                "Studio",
                "23",
                id * 10,
                "Studio name $id",
                "Studio address $id",
                null,
                null,
                null)

        private fun getPageOffset(pageNumber: Int, queryLimit: Int) = pageNumber * queryLimit

        private fun List<PlaceApiResponse>.getAllPlaceApiAsList(): List<PlaceApi> {
            val list = mutableListOf<PlaceApi>()
            forEach { placeAPiResponse ->
                placeAPiResponse.places?.let { list.addAll(it.filterNotNull()) }
            }
            return list
        }
    }
}