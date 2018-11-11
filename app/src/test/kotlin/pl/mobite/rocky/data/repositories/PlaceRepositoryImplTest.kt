package pl.mobite.rocky.data.repositories

import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.repositories.models.Place
import pl.mobite.rocky.data.remote.backend.responses.CoordinatesBackendResponse
import pl.mobite.rocky.data.remote.backend.responses.LifeSpanBackendResponse
import pl.mobite.rocky.data.remote.repository.PlaceRemoteRepository
import pl.mobite.rocky.utils.createSamplePlaceAPI
import pl.mobite.rocky.utils.lazyMock


class PlaceRepositoryImplTest {

    private val placeRemoteRepositoryMock: PlaceRemoteRepository by lazyMock()

    private lateinit var repository: PlaceRepositoryImpl
    private lateinit var testObserver: TestObserver<List<Place>>

    @Before
    fun setUp() {
        repository = PlaceRepositoryImpl(placeRemoteRepositoryMock)
        testObserver = TestObserver()
    }

    @Test
    fun testGetPlacesFrom1990Success() {
        `when`(placeRemoteRepositoryMock.fetchAllPlacesFrom1990(dummyQuery)).thenReturn(Single.just(
            dummyPlaceApiList
        ))

        repository.getPlacesFrom1990(dummyQuery)
                .subscribe(testObserver)

        testObserver.assertValue(dummyPlaceListExpected)
        testObserver.assertNoErrors()
        testObserver.assertComplete()
    }

    @Test
    fun testGetPlacesFrom1990SuccessButEmptyList() {
        `when`(placeRemoteRepositoryMock.fetchAllPlacesFrom1990(dummyQuery)).thenReturn(Single.just(emptyList()))

        repository.getPlacesFrom1990(dummyQuery)
                .subscribe(testObserver)

        testObserver.assertValue(emptyList())
        testObserver.assertNoErrors()
        testObserver.assertComplete()
    }

    @Test
    fun testGetPlacesFrom1990Failure() {
        `when`(placeRemoteRepositoryMock.fetchAllPlacesFrom1990(dummyQuery)).thenReturn(Single.error(
            dummyException
        ))

        repository.getPlacesFrom1990(dummyQuery)
                .subscribe(testObserver)

        testObserver.assertError(dummyException)
        testObserver.assertNotComplete()
    }

    companion object {

        private val dummyException = Exception("test exception")
        private const val dummyQuery = "query"

        private val dummyPlaceApi = createSamplePlaceAPI("Sample name", "14.2", "-12.3", "1993")
        private val dummyPlaceExpected = Place("Sample name", 1993, 14.2, -12.3)

        private val dummyPlaceApiInvalid1 = createSamplePlaceAPI(null, "14.2", "-12.3", "1993")
        private val dummyPlaceApiInvalid2 = createSamplePlaceAPI("Sample name 1", "14.2", "-12.3", null)
        private val dummyPlaceApiInvalid3 = createSamplePlaceAPI("Sample name 2", null, "-12.3", "1993")
        private val dummyPlaceApiInvalid4 = createSamplePlaceAPI("Sample name 3", "14.2", null, "1993")
        private val dummyPlaceApiInvalid5 = createSamplePlaceAPI("Sample name 4",
            CoordinatesBackendResponse("14.2", "-12.3"), null)
        private val dummyPlaceApiInvalid6 = createSamplePlaceAPI("Sample name 5", null,
            LifeSpanBackendResponse("1993", null, null)
        )

        private val dummyPlaceApiList = listOf(
            dummyPlaceApiInvalid1,
            dummyPlaceApiInvalid2,
            dummyPlaceApi,
            dummyPlaceApiInvalid3,
            dummyPlaceApiInvalid4,
            dummyPlaceApiInvalid5,
            dummyPlaceApiInvalid6
        )
        private val dummyPlaceListExpected = listOf(dummyPlaceExpected)
    }
}