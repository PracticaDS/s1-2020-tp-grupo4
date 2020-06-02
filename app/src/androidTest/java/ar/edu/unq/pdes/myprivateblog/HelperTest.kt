package ar.edu.unq.pdes.myprivateblog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.assertion.WebViewAssertions
import androidx.test.espresso.web.model.Atoms
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString


fun Int.isMatchingWithValue(anStringValue : String): ViewInteraction = onView(withId(this)).check(matches(withText(anStringValue)))

fun Int.clickButton() : ViewInteraction = onView(withId(this)).perform(click())

fun Int.isDisplayedInView() : ViewInteraction = onView(withId(this)).check(matches(isDisplayed()))

fun Int.isInvisibleInView() : ViewInteraction = onView(withId(this)).check((matches(withEffectiveVisibility((Visibility.INVISIBLE)))))

fun Int.isGoneInView() : ViewInteraction = onView(withId(this)).check((matches(withEffectiveVisibility((Visibility.GONE)))))

fun Int.fillText(text : String) : ViewInteraction = onView(withId(this)).perform(typeText(text))

fun Int.clearText() : ViewInteraction = onView(withId(this)).perform(ViewActions.clearText())

fun Int.isMatchingWithHint(hintResource : Int): ViewInteraction = onView(withId(this)).check(matches(withHint(hintResource)))

fun Int.viewIsMatchingWithValue(anStringValue : String): ViewInteraction = onView(allOf(withId(this), isDisplayed())).check(matches(withText(anStringValue)))

fun Int.webViewIsMatchingWithValue(anStringValue : String): Web.WebInteraction<String> =
    Web.onWebView(withId(this))
        .withElement(DriverAtoms.findElement(Locator.TAG_NAME, "html"))
        .check(
            WebViewAssertions.webMatches(
                Atoms.getCurrentUrl(),
                containsString(anStringValue)
            )
        )

fun checkSnackBarMainText(hintMainText : Int) : ViewInteraction = onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(hintMainText)))

fun checkSnackBarUndoText(hintActionText : Int): ViewInteraction = onView(withId(com.google.android.material.R.id.snackbar_action)).check(matches(withText(hintActionText)))

fun clickActualSnackBar() : ViewInteraction = onView(withId(com.google.android.material.R.id.snackbar_action)).perform(click())