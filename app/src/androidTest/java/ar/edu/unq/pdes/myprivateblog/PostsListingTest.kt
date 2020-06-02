package ar.edu.unq.pdes.myprivateblog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.withId
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

    //TODO: Test que corrobora empty_state_view
    //@Test
    //fun whenOpenAppWithNoPosts_emptyStateViewShouldDisplay() {
        //R.id.empty_state_view.isDisplayedInView()
        //R.id.posts_list_recyclerview.isGoneInView()
    //}

    @Test
    fun whenPostsAreAdded_emptyStateViewShouldNotDisplay() {
        R.id.create_new_post.clickButton()
        R.id.btn_save.clickButton()
        R.id.btn_back.clickButton()

        R.id.empty_state_view.isGoneInView()
        R.id.posts_list_recyclerview.isDisplayedInView()
    }

    @Test
    fun whenEnteringPostListing_shouldDisplayTheToolbarAndNotNavigationDrawer(){
        R.id.general_toolbar.isDisplayedInView()
        R.id.nav_view.isInvisibleInView()
    }

    @Test
    fun whenSwipingRight_shouldDisplayNavigationDrawer(){
        onView(withId(R.id.main_activity)).perform(DrawerActions.open())
        R.id.nav_view.isDisplayedInView()
    }


}