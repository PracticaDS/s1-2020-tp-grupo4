package ar.edu.unq.pdes.myprivateblog

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import ar.edu.unq.pdes.myprivateblog.di.ApplicationComponent
import ar.edu.unq.pdes.myprivateblog.di.DaggerApplicationComponent
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesSyncingService
import ar.edu.unq.pdes.myprivateblog.services.DownloadImageTask
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import ar.edu.unq.pdes.myprivateblog.services.drive.GoogleDriveService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_toolbar.*
import timber.log.Timber
import javax.inject.Inject


class MainActivity @Inject constructor() : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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

        val serverClientId = getString(R.string.web_client_id)
        this.gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
            .requestServerAuthCode(serverClientId)
            .requestEmail()
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
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

    fun sync() {
        val secretKey = viewModel.encryptionService.retrieveSecretKey()
        if (secretKey != null) {
            viewModel.blogEntriesSyncingService.fetchAndStoreBlogEntries(secretKey)
        } else {
            Snackbar.make(
                findViewById(R.id.layout),
                R.string.could_not_find_secret_key, Snackbar.LENGTH_LONG)
                .show();
            viewModel.googleDriveService.fetchAndStoreSecretKey()
        }
    }

    fun initDataAndShowSliderMenu(name: String, email: String, photoUri: Uri?){
        supportActionBar?.title = "My Private Blog"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_drawer)
        findViewById<TextView>(R.id.user_name).text = name
        findViewById<TextView>(R.id.user_mail).text = email
        if(photoUri != null){
            DownloadImageTask(findViewById(R.id.profile_photo)).execute(photoUri.toString());
        }
        supportActionBar?.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout_button -> {
                signOut()
            }

            R.id.sync_button -> {
                sync()
            }
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
