package ar.edu.unq.pdes.myprivateblog

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import ar.edu.unq.pdes.myprivateblog.screens.post_create.PostCreateFragment
import com.google.android.material.navigation.NavigationView
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_toolbar.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel by viewModels<MainActivityViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        RxJavaPlugins.setErrorHandler { Timber.e(it) }

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = "My Private Blog"
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_drawer)

        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle (
            this,
            drawyer_layout,
            toolbar,
            R.string.open_toolbar,
            R.string.close_toolbar
        ) {

        }

        drawerToggle.isDrawerIndicatorEnabled = true
        drawyer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    fun hideKeyboard() {
        val imm: InputMethodManager =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = this.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.login_button -> {
                TODO("not implemented")
            }
            R.id.logout_button -> {
                TODO("not implemented")
            }
            R.id.sync_button -> {
                TODO("not implemented")
            }
        }
        drawyer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawyer_layout.isDrawerOpen(GravityCompat.START)) {
            drawyer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}
