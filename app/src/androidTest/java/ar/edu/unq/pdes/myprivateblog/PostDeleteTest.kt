package ar.edu.unq.pdes.myprivateblog

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
        R.id.btn_testing.clickButton()
        R.id.create_new_post.clickButton()
        R.id.title.fillText("An Title")
        R.id.body.fillText("An body Text")
        R.id.btn_save.clickButton()
    }

    @Test
    fun whenDeletePost_shouldShowButtonAndDeleteSuccessfully(){
        val initialSize = blogEntriesService.getDataCount()
        R.id.btn_delete.isDisplayedInView()
        R.id.btn_delete.clickButton()
        val finalSize = blogEntriesService.getDataCount()
        Assert.assertSame(initialSize - 1, finalSize)
    }

    @Test
    fun whenDeletePost_shouldShowSnackBarUndo(){
        R.id.btn_delete.clickButton()
        checkSnackBarMainText(R.string.post_deleted_successful)
        checkSnackBarUndoText(R.string.undo_action)
    }

    @Test
    fun whenDeletePost_shouldUndoThrowsSnackbar(){
        val initialSize = blogEntriesService.getDataCount()
        R.id.btn_delete.clickButton()
        clickActualSnackBar()
        val finalSize = blogEntriesService.getDataCount()
        Assert.assertSame(initialSize, finalSize)
    }

}