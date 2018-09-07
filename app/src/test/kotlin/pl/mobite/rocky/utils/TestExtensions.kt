package pl.mobite.rocky.utils

import org.mockito.Mockito


/**
 * Lazy delegate for creating mocks
 */
inline fun <reified T : Any> lazyMock(): Lazy<T> = lazy { Mockito.mock(T::class.java) }

fun <T> List<T>.distinctUntilChanged(): List<T> {
    val distinctUtilChangedList = mutableListOf<T>()
    forEachIndexed { i, item ->
        val prevItem = getOrNull(i -1)
        if (prevItem == null || prevItem != item) {
            distinctUtilChangedList.add(item)
        }
    }
    return distinctUtilChangedList
}