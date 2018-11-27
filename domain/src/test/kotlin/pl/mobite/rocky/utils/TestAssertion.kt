package pl.mobite.rocky.utils

import org.junit.Assert
import pl.mobite.rocky.ui.components.map.MapResult
import pl.mobite.rocky.ui.components.map.MapResult.LoadPlacesResult


/**
 * Custom assert function for MapResult
 */
fun assertMapResult(expected: MapResult, tested: MapResult) {
    when (expected) {
        is LoadPlacesResult.Success -> {
            Assert.assertTrue(tested is MapResult.LoadPlacesResult.Success)
            tested as LoadPlacesResult.Success
            Assert.assertEquals(expected.places, tested.places)
        }
        else -> Assert.assertEquals(expected, tested)
    }
}