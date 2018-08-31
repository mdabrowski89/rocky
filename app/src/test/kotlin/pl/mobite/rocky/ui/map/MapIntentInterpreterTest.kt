package pl.mobite.rocky.ui.map

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.ui.map.MapAction.*
import pl.mobite.rocky.ui.map.MapIntent.*


class MapIntentInterpreterTest {

    private lateinit var mapIntentInterpreter: MapIntentInterpreter

    @Before
    fun setUp() {
        mapIntentInterpreter = MapIntentInterpreter()
    }

    @Test
    fun testMapIntentInterpreter() {
        assertEquals(ReRenderAction, mapIntentInterpreter.apply(MapReadyIntent))

        assertEquals(LoadPlacesAction(dummyQuery), mapIntentInterpreter.apply(SearchPlacesIntent(dummyQuery)))

        assertEquals(ClearSearchResultsAction, mapIntentInterpreter.apply(AllMarkersGoneIntent))

        assertEquals(ClearErrorAction, mapIntentInterpreter.apply(ErrorDisplayedIntent))
    }
}