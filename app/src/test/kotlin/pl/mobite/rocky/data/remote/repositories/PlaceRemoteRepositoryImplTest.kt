package pl.mobite.rocky.data.remote.repositories

import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.remote.backend.MusicBrainzBackend
import pl.mobite.rocky.data.remote.backend.responses.CoordinatesBackendResponse
import pl.mobite.rocky.data.remote.backend.responses.LifeSpanBackendResponse
import pl.mobite.rocky.data.remote.backend.responses.PlaceBackendResponse
import pl.mobite.rocky.data.remote.backend.responses.PlacesBackendResponse
import pl.mobite.rocky.data.remote.repository.PlaceRemoteRepositoryImpl
import pl.mobite.rocky.data.remote.repository.toPlace
import pl.mobite.rocky.data.repositories.models.Place
import pl.mobite.rocky.utils.lazyMock


class PlaceRemoteRepositoryImplTest {

    private val musicBrainzBackendMock: MusicBrainzBackend by lazyMock()

    private lateinit var remoteRepository: PlaceRemoteRepositoryImpl
    private lateinit var testObserver: TestObserver<List<Place>>

    private val pageLimit = 10

    @Before
    fun setUp() {
        remoteRepository = PlaceRemoteRepositoryImpl(musicBrainzBackendMock, pageLimit, 0)
        testObserver = TestObserver()
    }

    @Test
    fun testOnePageQuery() {
        val count = 7
        val placeApiResponseList = createDummyPlaceApiResponseList(count, pageLimit)
        val placeListExpected = placeApiResponseList.getAllPlaceApiAsList()
        placeApiResponseList.forEachIndexed { i, list ->
            `when`(
                musicBrainzBackendMock.getPlaces(
                    dummyQuery.withYearFilter(),
                    getPageOffset(i, pageLimit),
                    pageLimit
                )
            ).thenReturn(list)
        }

        val places = remoteRepository.fetchAllPlacesFrom1990(dummyQuery)

        assertEquals(placeListExpected, places)
    }

    @Test
    fun testMultiPageQuery() {
        val count = 65
        val placeApiResponseList = createDummyPlaceApiResponseList(count, pageLimit)
        val placeApiListExpected = placeApiResponseList.getAllPlaceApiAsList()
        placeApiResponseList.forEachIndexed { i, list ->
            `when`(
                musicBrainzBackendMock.getPlaces(
                    dummyQuery.withYearFilter(),
                    getPageOffset(i, pageLimit),
                    pageLimit
                )
            )
                .thenReturn(list)
        }

        val placesApiListTested = remoteRepository.fetchAllPlacesFrom1990(dummyQuery)

        assertEquals(placeApiListExpected, placesApiListTested)
    }

    @Test
    fun testOnePageQueryError() {
        `when`(musicBrainzBackendMock.getPlaces(dummyQuery.withYearFilter(), 0, pageLimit))
            .thenThrow(dummyException)

        try {
            remoteRepository.fetchAllPlacesFrom1990(dummyQuery)
            assertTrue("dummy exception should be thrown", false)
        } catch (e: Throwable) {
            assertTrue(e == dummyException)
        }
    }


    @Test
    fun testMultiPageQueryError() {
        val count = 65
        val placeApiResponseList = createDummyPlaceApiResponseList(count, pageLimit)
        placeApiResponseList.forEachIndexed { i, list ->
            if (i < placeApiResponseList.size - 1) {
                `when`(
                    musicBrainzBackendMock.getPlaces(
                        dummyQuery.withYearFilter(),
                        getPageOffset(i, pageLimit),
                        pageLimit
                    )
                ).thenReturn(list)
            } else {
                `when`(
                    musicBrainzBackendMock.getPlaces(
                        dummyQuery.withYearFilter(),
                        getPageOffset(i, pageLimit),
                        pageLimit
                    )
                ).thenThrow(dummyException)
            }
        }

        try {
            remoteRepository.fetchAllPlacesFrom1990(dummyQuery)
            assertTrue("dummy exception should be thrown", false)
        } catch (e: Throwable) {
            assertTrue(e == dummyException)
        }
    }

    companion object {

        private const val dummyQuery = "query"
        private val dummyException = Throwable("dummy exception")

        private fun String.withYearFilter() = "$this AND begin:[1990 TO 2200]"

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
            id.toString(), "Studio", "23", id * 10, "Studio name $id", "Studio address $id",
            CoordinatesBackendResponse("0.0", "0.0"), null,
            LifeSpanBackendResponse("1960", null, false)
        )

        private fun getPageOffset(pageNumber: Int, queryLimit: Int) = pageNumber * queryLimit

        private fun List<PlacesBackendResponse>.getAllPlaceApiAsList(): List<Place> {
            val list = mutableListOf<Place>()
            forEach { placeAPiResponse ->
                placeAPiResponse.placeResponses?.let { backendResponses ->
                    list.addAll(backendResponses.mapNotNull { backendPlace ->
                        backendPlace?.toPlace()
                    }) }
            }
            return list
        }
    }
}