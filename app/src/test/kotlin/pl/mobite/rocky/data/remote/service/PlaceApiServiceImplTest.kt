package pl.mobite.rocky.data.remote.service

import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.data.remote.MusicBrainzService
import pl.mobite.rocky.data.remote.models.PlaceApi
import pl.mobite.rocky.data.remote.services.PlaceApiServiceImpl
import pl.mobite.rocky.utils.lazyMock


class PlaceApiServiceImplTest {

    private val musicBrainzServiceMock: MusicBrainzService by lazyMock()

    private lateinit var placeApiServiceImpl: PlaceApiServiceImpl
    private lateinit var testObserver: TestObserver<List<PlaceApi>>

    @Before
    fun setUp() {
        placeApiServiceImpl = PlaceApiServiceImpl({ musicBrainzServiceMock }, 0)
        testObserver = TestObserver()
    }

    @Test
    fun testOnePageQuery() {
        // TODO: add test
    }

    @Test
    fun testMultiPageQuery() {
        // TODO: add test
    }

    @Test
    fun testQueryError() {
        // TODO: add test
    }
}