package pl.mobite.rocky.ui.components.map

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.mobite.rocky.ui.components.map.MapAction.*
import pl.mobite.rocky.ui.components.map.MapIntent.*


class MapIntentInterpreterTest {

    private lateinit var interpreter: MapIntentInterpreter

    @Before
    fun setUp() {
        interpreter = MapIntentInterpreter()
    }

    @Test
    fun testMapIntentInterpreter() {
        assertEquals(ReRenderAction, interpreter.apply(MapReadyIntent))

        assertEquals(LoadPlacesAction(dummyQuery), interpreter.apply(SearchPlacesIntent(dummyQuery)))

        assertEquals(ClearSearchResultsAction, interpreter.apply(AllMarkersGoneIntent))
    }

    companion object {

        private const val dummyQuery = "query"
    }
}