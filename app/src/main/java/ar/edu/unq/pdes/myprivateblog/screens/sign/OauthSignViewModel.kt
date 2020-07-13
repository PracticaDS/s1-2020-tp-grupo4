package ar.edu.unq.pdes.myprivateblog.screens.sign

import android.content.Context
import androidx.fragment.app.Fragment
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesSyncingService
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import ar.edu.unq.pdes.myprivateblog.services.drive.GoogleDriveService
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class OauthSignViewModel @Inject constructor(
    blogEntriesService: BlogEntriesService,
    private val googleDriveService: GoogleDriveService,
    private val blogEntriesSyncingService: BlogEntriesSyncingService,
    private val encryptionService: EncryptionService,
    context: Context
): BaseViewModel(blogEntriesService, context) {

    fun checkAndCreateSecretKey() {
        if (SYNCING_FEATURE_ENABLED) {
            googleDriveService.fetchAndStoreSecretKey()
        }
    }

    fun uploadPosts(fragment: Fragment) {
        if (SYNCING_FEATURE_ENABLED) {
            val secretKey = encryptionService.retrieveSecretKey()
            if (secretKey != null) {
                blogEntriesSyncingService.uploadUnsyncedBlogEntries(secretKey)
                Snackbar.make(
                    fragment.view!!,
                    R.string.uploading_posts, Snackbar.LENGTH_LONG)
                    .show();
            } else {
                Snackbar.make(
                    fragment.view!!,
                    R.string.could_not_find_secret_key, Snackbar.LENGTH_LONG)
                    .show();
            }
        }
    }
}