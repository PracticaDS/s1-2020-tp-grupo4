package ar.edu.unq.pdes.myprivateblog

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.web.assertion.WebViewAssertions
import androidx.test.espresso.web.model.Atoms
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import org.hamcrest.CoreMatchers.containsString

fun Int.isMatchingWithValue(anStringValue : String): ViewInteraction = onView(ViewMatchers.withId(this)).check(ViewAssertions.matches(ViewMatchers.withText(anStringValue)))

fun Int.clickButton() : ViewInteraction = onView(ViewMatchers.withId(this)).perform(click())

fun Int.clickPopupButton() : ViewInteraction = onView(ViewMatchers.withId(this)).inRoot(isPlatformPopup()).perform(click())

fun Int.isDisplayedInView() : ViewInteraction = onView(ViewMatchers.withId(this)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

fun Int.isDisplayedInPopup() : ViewInteraction = onView(ViewMatchers.withId(this)).inRoot(isPlatformPopup()).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

fun Int.fillText(text : String) : ViewInteraction = onView(ViewMatchers.withId(this)).perform(ViewActions.typeText(text))

fun Int.clearText() : ViewInteraction = onView(ViewMatchers.withId(this)).perform(ViewActions.clearText())

fun Int.isMatchingWithHint(hintResource : Int): ViewInteraction = Espresso.onView(ViewMatchers.withId(this)).check(ViewAssertions.matches(ViewMatchers.withHint(hintResource)))

fun Int.webViewIsMatchingWithValue(anStringValue : String): Web.WebInteraction<String> =
    Web.onWebView(withId(this))
        .withElement(DriverAtoms.findElement(Locator.TAG_NAME, "html"))
        .check(
            WebViewAssertions.webMatches(
                Atoms.getCurrentUrl(),
                containsString(anStringValue)
            )
        )