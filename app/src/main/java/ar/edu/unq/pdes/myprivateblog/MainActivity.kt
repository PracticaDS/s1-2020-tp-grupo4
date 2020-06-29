package ar.edu.unq.pdes.myprivateblog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_toolbar.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel by viewModels<MainActivityViewModel> { viewModelFactory }

    var RC_SIGN_IN = 0
    lateinit var gso : GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        RxJavaPlugins.setErrorHandler { Timber.e(it) }

        setContentView(R.layout.activity_main)

        setSupportActionBar(general_toolbar)
        supportActionBar?.hide()

        this.gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle (
            this,
            main_activity,
            general_toolbar,
            R.string.open_toolbar,
            R.string.close_toolbar
        ) {

        }

        drawerToggle.isDrawerIndicatorEnabled = true
        main_activity.addDrawerListener(drawerToggle)
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

    fun signOut(){
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient.signOut().let{
            val z = Intent(this, MainActivity::class.java)
            z.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(z)
        }
    }

    fun initDataAndShowSliderMenu(name: String, email: String){
        supportActionBar?.title = "My Private Blog"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_drawer)
        findViewById<TextView>(R.id.user_name).text = name
        findViewById<TextView>(R.id.user_mail).text = email
        supportActionBar?.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout_button -> {
                signOut()
            }

            /* TODO: enlazar sync_button a funcionalidad
            R.id.sync_button -> {
            }
             */
        }
        main_activity.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (main_activity.isDrawerOpen(GravityCompat.START)) {
            main_activity.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}
