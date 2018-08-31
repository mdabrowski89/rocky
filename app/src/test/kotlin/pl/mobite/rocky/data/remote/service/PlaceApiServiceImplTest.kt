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

    private fun String.withYearFilter() = "$this AND begin:[1990 TO 2200]"
}