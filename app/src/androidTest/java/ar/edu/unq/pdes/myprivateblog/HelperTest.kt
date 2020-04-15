package ar.edu.unq.pdes.myprivateblog

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers

fun Int.isMatchingWithValue(anStringValue : String): ViewInteraction = Espresso.onView(ViewMatchers.withId(this)).check(ViewAssertions.matches(ViewMatchers.withText(anStringValue)))

fun Int.clickButton() : ViewInteraction = Espresso.onView(ViewMatchers.withId(this)).perform(ViewActions.click())

fun Int.isDisplayedInView() : ViewInteraction = Espresso.onView(ViewMatchers.withId(this)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

fun Int.fillText(text : String) : ViewInteraction = Espresso.onView(ViewMatchers.withId(this)).perform(ViewActions.typeText(text))

fun Int.isMatchingWithHint(hintResource : Int): ViewInteraction = Espresso.onView(ViewMatchers.withId(this)).check(ViewAssertions.matches(ViewMatchers.withHint(hintResource)))