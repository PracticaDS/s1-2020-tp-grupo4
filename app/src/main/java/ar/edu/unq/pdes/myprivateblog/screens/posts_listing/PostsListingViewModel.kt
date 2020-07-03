package ar.edu.unq.pdes.myprivateblog.screens.posts_listing

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import ar.edu.unq.pdes.myprivateblog.BaseFragment
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesSyncingService
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import ar.edu.unq.pdes.myprivateblog.services.drive.GoogleDriveService
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class PostsListingViewModel @Inject constructor(
    blogEntriesService: BlogEntriesService,
    private val blogEntriesSyncingService: BlogEntriesSyncingService,
    val encryptionService: EncryptionService,
    private val googleDriveService: GoogleDriveService,
    context: Context
): BaseViewModel(blogEntriesService, context) {

    val posts: LiveData<List<BlogEntry>> by lazy {
        blogEntriesService.getAll()
    }

    fun sync(fragment: BaseFragment) {
        if (SYNCING_FEATURE_ENABLED) {
            val secretKey = encryptionService.retrieveSecretKey()
            if (secretKey != null) {
                blogEntriesSyncingService.fetchAndStoreBlogEntries(secretKey)
            } else {
                Snackbar.make(fragment.view!!, R.string.could_not_sync_try_again, Snackbar.LENGTH_LONG)
                    .show();
                googleDriveService.fetchAndStoreSecretKey()
            }
        }
    }

}
