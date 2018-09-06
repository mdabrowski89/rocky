package pl.mobite.rocky.ui.components.map

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.mobite.rocky.R


@RunWith(AndroidJUnit4::class)
@LargeTest
class MapActivityTest {

    // TODO: find a way to replace PlaceRepository in MapViewModel into fake one - dagger injection?

    @get:Rule
    var mActivityRule: ActivityTestRule<MapActivity> = ActivityTestRule(MapActivity::class.java)

    @Test
    fun testLoadingData() {
        /* perform query with results */
        onView(withId(R.id.queryInput))
                .perform(typeText("germany"), pressImeActionButton())

        /* verify toast with empty list message is not displayed */
        onView(withText(R.string.map_api_empty_list)).check(doesNotExist())

        /* perform query with no results */
        onView(withId(R.id.queryInput))
                .perform(replaceText("asd"), pressImeActionButton())

        /* verify toast with empty list message is displayed */
        onView(withText(R.string.map_api_empty_list))
                .inRoot(withDecorView(not(`is`(mActivityRule.activity.window.decorView)))).check(matches(isDisplayed()))
    }
}