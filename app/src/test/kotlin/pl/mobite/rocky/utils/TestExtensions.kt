package pl.mobite.rocky.utils

import org.mockito.Mockito




/**
 * Lazy delegate for creating mocks
 */
inline fun <reified T : Any> lazyMock(): Lazy<T> = lazy { Mockito.mock(T::class.java) }

