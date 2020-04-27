package ar.edu.unq.pdes.myprivateblog

import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms.getCurrentUrl
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.matchers.JUnitMatchers.containsString
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PostEditTest : BaseInjectedTest() {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    private val postTitle = "Test title post"
    private val bodyText  = "Test body text"

    private val postTitleEdited = "Test title post edited"
    private val bodyTextEdited  = "Test body text edited"

    @Before
    fun setup() {
        R.id.create_new_post.clickButton()
        R.id.title.fillText(postTitle)
        R.id.body.fillText(bodyText)
        R.id.btn_save.clickButton()
    }

    @Test
    fun whenTappingOnEditButtonOnDetailPage_postEditionScreenShouldOpen() {
        R.id.btn_edit.clickButton()

        R.id.btn_close.isDisplayedInView()
        R.id.btn_save.isDisplayedInView()
        R.id.title.isDisplayedInView()
        R.id.body.isDisplayedInView()

        R.id.title.isMatchingWithValue(postTitle)
        R.id.body.webViewIsMatchingWithValue(bodyText)
    }

    @Test
    fun whenTappingOnBackButtonOnDetailPage_theUserShouldBeRedirectedAndThePostShouldRemainUnchanged() {
        R.id.btn_edit.clickButton()
        R.id.btn_close.clickButton()

        R.id.btn_edit.isDisplayedInView()
        R.id.btn_back.isDisplayedInView()

        R.id.title.isMatchingWithValue(postTitle)
        R.id.body.webViewIsMatchingWithValue(bodyText)
    }

    @Test
    fun whenModifyingDataAndSubmitting_postDetailScreenShouldOpenAndDisplayChangedValues() {
        R.id.btn_edit.clickButton()

        R.id.title.clearText()
        R.id.title.fillText(postTitleEdited)
        R.id.body.clearText()
        R.id.body.fillText(bodyTextEdited)

        R.id.btn_save.clickButton()

        R.id.btn_back.isDisplayedInView()
        R.id.btn_edit.isDisplayedInView()

        R.id.title.isMatchingWithValue(postTitleEdited)
        R.id.body.webViewIsMatchingWithValue(bodyTextEdited)
    }

    @Test
    fun whenModifyingDataAndSubmitting_postsTotalCountShouldRemainTheSame() {
        var initialSize = blogEntriesService.getDataCount()
        R.id.btn_edit.clickButton()

        R.id.title.clearText()
        R.id.title.fillText(postTitleEdited)
        R.id.body.clearText()
        R.id.body.fillText(bodyTextEdited)

        R.id.btn_save.clickButton()

        val finalSize = blogEntriesService.getDataCount()
        Assert.assertSame(initialSize, finalSize)
    }
}