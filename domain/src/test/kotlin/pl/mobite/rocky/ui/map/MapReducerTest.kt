package pl.mobite.rocky.ui.map

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.data.models.Place


class MapReducerTest {

    private lateinit var mapReducer: MapReducer
    private lateinit var initialState: MapViewState

    @Before
    fun setUp() {
        mapReducer = MapReducer()
        initialState = MapViewState.default()
    }

    @Test
    fun testMapReadyResult() {
        val newState = mapReducer.apply(initialState, MapResult.MapReadyResult)

        Assert.assertEquals(initialState.places, newState.places)
        Assert.assertEquals(initialState.error, newState.error)
        Assert.assertEquals(initialState.isLoading, newState.isLoading)
        Assert.assertEquals(initialState.placesTimestamp, newState.placesTimestamp)
        Assert.assertEquals(!initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultInFlight() {
        val newState = mapReducer.apply(initialState, MapResult.LoadPlacesResult.InFlight)

        Assert.assertEquals(initialState.places, newState.places)
        Assert.assertEquals(null, newState.error)
        Assert.assertEquals(true, newState.isLoading)
        Assert.assertEquals(initialState.placesTimestamp, newState.placesTimestamp)
        Assert.assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultSuccess() {
        val newState = mapReducer.apply(initialState, MapResult.LoadPlacesResult.Success(places, testTimestamp))

        Assert.assertEquals(places, newState.places)
        Assert.assertEquals(null, newState.error)
        Assert.assertEquals(false, newState.isLoading)
        Assert.assertEquals(testTimestamp, newState.placesTimestamp)
        Assert.assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultSuccessButEmptyList() {
        val newState = mapReducer.apply(initialState, MapResult.LoadPlacesResult.Success(emptyPlaces, testTimestamp))

        Assert.assertEquals(emptyPlaces, newState.places)
        Assert.assertEquals(null, newState.error)
        Assert.assertEquals(false, newState.isLoading)
        Assert.assertEquals(testTimestamp, newState.placesTimestamp)
        Assert.assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testLoadPlacesResultFailure() {
        val newState = mapReducer.apply(initialState, MapResult.LoadPlacesResult.Failure(testError))

        Assert.assertEquals(initialState.places, newState.places)
        Assert.assertEquals(testError, newState.error)
        Assert.assertEquals(false, newState.isLoading)
        Assert.assertEquals(initialState.placesTimestamp, newState.placesTimestamp)
        Assert.assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testAllPlacesGoneResult() {
        val newState = mapReducer.apply(initialState, MapResult.AllPlacesGoneResult)

        Assert.assertEquals(emptyList<Place>(), newState.places)
        Assert.assertEquals(initialState.error, newState.error)
        Assert.assertEquals(initialState.isLoading, newState.isLoading)
        Assert.assertEquals(null, newState.placesTimestamp)
        Assert.assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }

    @Test
    fun testErrorDisplayedResult() {
        val newState = mapReducer.apply(initialState, MapResult.ErrorDisplayedResult)

        Assert.assertEquals(initialState.places, newState.places)
        Assert.assertEquals(null, newState.error)
        Assert.assertEquals(initialState.isLoading, newState.isLoading)
        Assert.assertEquals(initialState.placesTimestamp, newState.placesTimestamp)
        Assert.assertEquals(initialState.reRenderFlag, newState.reRenderFlag)
    }
}