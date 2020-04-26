package ar.edu.unq.pdes.myprivateblog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isFocusable
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
class PostsDeleteTest : BaseInjectedTest() {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        R.id.create_new_post.clickButton()
        R.id.title.fillText("An Title")
        R.id.body.fillText("An body Text")
        R.id.btn_save.clickButton()
    }

    @Test
    fun whenDeletePost_shouldShowButtonAndDeleteSuccessfully(){
        var initialSize = blogEntriesService.getDataCount()
        R.id.btn_delete.isDisplayedInView()
        R.id.btn_delete.clickButton()
        R.id.btn_delete_confirm.isDisplayedInPopup()
        R.id.btn_delete_confirm.clickPopupButton()
        val finalSize = blogEntriesService.getDataCount()
        Assert.assertSame(initialSize - 1, finalSize)
    }

}