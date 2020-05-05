package ar.edu.unq.pdes.myprivateblog

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
    fun whenOpenApp_createButtonShouldOpen() {
        R.id.create_new_post.isDisplayedInView()
    }

    @Test
    fun whenOpenAppWithNoPosts_emptyStateViewShouldDisplay() {
        R.id.empty_state_view.isDisplayedInView()
        R.id.posts_list_recyclerview.isGoneInView()
    }

    @Test
    fun whenPostsAreAdded_emptyStateViewShouldNotDisplay() {
        R.id.create_new_post.clickButton()
        R.id.btn_save.clickButton()
        R.id.btn_back.clickButton()

        R.id.empty_state_view.isGoneInView()
        R.id.posts_list_recyclerview.isDisplayedInView()
    }

}