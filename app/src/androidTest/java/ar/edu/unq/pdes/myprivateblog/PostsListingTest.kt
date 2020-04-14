package ar.edu.unq.pdes.myprivateblog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PostsListingTest {

    
    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun whenTappingOnNewPostFab_postCreationScreenShouldOpen() {
        R.id.create_new_post.clickButton()
        R.id.title.isMatchingWithHint(R.string.hint_post_title)
    }

    @Test
    fun whenCreatingPost_shouldNavigateToPostDetail(){
        val postTitle = "Test title post"
        val bodyText = "Test body text"

        R.id.create_new_post.clickButton()
        R.id.title.fillText(postTitle)
        R.id.body.fillText(bodyText)

        R.id.btn_save.isDisplayedInView()
        R.id.btn_save.clickButton()

        R.id.title.isMatchingWithValue(postTitle)
    }
}

fun Int.isMatchingWithValue(anStringValue : String): ViewInteraction = onView(withId(this)).check(matches(withText(anStringValue)))
fun Int.clickButton() : ViewInteraction = onView(withId(this)).perform(click())
fun Int.isDisplayedInView() : ViewInteraction = onView(withId(this)).check(matches(isDisplayed()))
fun Int.fillText(text : String) : ViewInteraction = onView(withId(this)).perform(typeText(text))
fun Int.isMatchingWithHint(hintResource : Int): ViewInteraction = onView(withId(this)).check(matches(withHint(hintResource)))