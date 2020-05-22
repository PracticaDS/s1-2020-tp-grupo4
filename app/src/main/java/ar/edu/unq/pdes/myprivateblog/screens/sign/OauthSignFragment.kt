package ar.edu.unq.pdes.myprivateblog.screens.sign

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ar.edu.unq.pdes.myprivateblog.BaseFragment
import ar.edu.unq.pdes.myprivateblog.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.oauth_sign.*


class OauthSignFragment : BaseFragment() {
    override val layoutId = R.layout.oauth_sign

    private val viewModel by viewModels<OauthSignViewModel> { viewModelFactory }

    private var RC_SIGN_IN = 0
    private lateinit var gso : GoogleSignInOptions

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        this.gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        sign_in_button.setOnClickListener {
            signIn()
        }

        sign_out_button.setOnClickListener{
            signOut()
        }
    }

    private fun signIn() {
        val mGoogleSignInClient = GoogleSignIn.getClient(getMainActivity(),gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut(){
        val mGoogleSignInClient = GoogleSignIn.getClient(getMainActivity(),gso)
        mGoogleSignInClient.signOut()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val auth = FirebaseAuth.getInstance()

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(view!!, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    fun updateUI(user: FirebaseUser?){
        if(user != null)
            findNavController().navigate(OauthSignFragmentDirections.signInButton())
    }
}