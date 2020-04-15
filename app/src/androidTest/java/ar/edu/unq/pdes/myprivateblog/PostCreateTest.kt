package ar.edu.unq.pdes.myprivateblog

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PostsCreateTest {

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