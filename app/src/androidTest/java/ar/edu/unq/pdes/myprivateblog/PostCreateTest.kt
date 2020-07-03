package ar.edu.unq.pdes.myprivateblog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PostsCreateTest : BaseInjectedTest(){

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        R.id.btn_testing.clickButton()
    }

    @Test
    fun whenTappingOnNewPostFab_postCreationScreenShouldOpen() {
        R.id.create_new_post.clickButton()
        R.id.title.isMatchingWithHint(R.string.hint_post_title)
    }
    
    @Test
    fun whenEnteringPostCreate_shouldShowActionButtons(){
        R.id.create_new_post.clickButton()
        R.id.btn_save.isDisplayedInView()
        R.id.btn_close.isDisplayedInView()
    }

    @Test
    fun whenCreatingPost_shouldNavigateToPostDetail(){
        val postTitle = "Test title post"
        val bodyText = "Test body text"

        R.id.create_new_post.clickButton()
        R.id.title.fillText(postTitle)
        R.id.body.fillText(bodyText)

        R.id.title.isMatchingWithValue(postTitle)
        R.id.body.isMatchingWithValue(bodyText)
    }

    @Test
    fun whenClickingBtnClose_shouldGoBack(){
        R.id.create_new_post.clickButton()
        R.id.btn_close.clickButton()

        R.id.create_new_post.isDisplayedInView()
    }

    @Test
    fun whenClickingSave_shouldSaveAndShowDetail(){
        var initialSize = blogEntriesService.getDataCount()
        val postTitle = "Test title post"
        val bodyText = "Test body text"

        R.id.create_new_post.clickButton()
        R.id.title.fillText(postTitle)
        R.id.body.fillText(bodyText)

        R.id.btn_save.clickButton()

        // Verify that saves information
        val finalSize = blogEntriesService.getDataCount()
        Assert.assertSame(initialSize + 1, finalSize)

        // Verify that shows button of post detail fragment
        R.id.btn_edit.isDisplayedInView()
        R.id.btn_back.isDisplayedInView()
        R.id.btn_delete.isDisplayedInView()

        // Verify that shows the correct information
        R.id.title.isMatchingWithValue(postTitle)
        R.id.body.webViewIsMatchingWithValue(bodyText)
    }

    @Test
    fun whenEnteringPostCreate_shouldNotShowTheToolbar(){
        R.id.create_new_post.clickButton()
        R.id.general_toolbar.isGoneInView()
    }

}