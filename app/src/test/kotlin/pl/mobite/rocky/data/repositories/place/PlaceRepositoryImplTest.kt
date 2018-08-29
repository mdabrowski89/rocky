package pl.mobite.rocky.data.repositories.place

import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import pl.mobite.rocky.data.models.Place
import pl.mobite.rocky.data.remote.services.PlaceApiService
import pl.mobite.rocky.utils.lazyMock


class PlaceRepositoryImplTest {

    private val placeApiServiceMock: PlaceApiService by lazyMock()

    private lateinit var placeRepositoryImpl: PlaceRepositoryImpl
    private lateinit var testObserver: TestObserver<List<Place>>

    @Before
    fun setUp() {
        placeRepositoryImpl = PlaceRepositoryImpl(placeApiServiceMock)
        testObserver = TestObserver()
    }

    @Test
    fun testGetPlacesFrom1990Success() {
        `when`(placeApiServiceMock.fetchAllPlacesFrom1990(testQuery)).thenReturn(Single.just(placeApiList))

        placeRepositoryImpl.getPlacesFrom1990(testQuery)
                .subscribe(testObserver)

        testObserver.assertValue(placeListExpected)
        testObserver.assertNoErrors()
        testObserver.assertComplete()
    }

    @Test
    fun testGetPlacesFrom1990SuccessButEmptyList() {
        `when`(placeApiServiceMock.fetchAllPlacesFrom1990(testQuery)).thenReturn(Single.just(emptyList()))

        placeRepositoryImpl.getPlacesFrom1990(testQuery)
                .subscribe(testObserver)

        testObserver.assertValue(emptyList())
        testObserver.assertNoErrors()
        testObserver.assertComplete()
    }

    @Test
    fun testGetPlacesFrom1990Failure() {
        `when`(placeApiServiceMock.fetchAllPlacesFrom1990(testQuery)).thenReturn(Single.error(testException))

        placeRepositoryImpl.getPlacesFrom1990(testQuery)
                .subscribe(testObserver)

        testObserver.assertError(testException)
        testObserver.assertNotComplete()
    }
}