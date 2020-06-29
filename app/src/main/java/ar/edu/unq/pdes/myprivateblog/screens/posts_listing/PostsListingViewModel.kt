package ar.edu.unq.pdes.myprivateblog.screens.posts_listing

import android.content.Context
import androidx.lifecycle.LiveData
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesSyncingService
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import ar.edu.unq.pdes.myprivateblog.services.drive.GoogleDriveService
import io.reactivex.Observable
import io.reactivex.ObservableSource
import timber.log.Timber
import java.util.concurrent.Callable
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

    fun sync() {
        if (SYNCING_FEATURE_ENABLED) {
            val secretKey = encryptionService.retrieveSecretKey()
            if (secretKey != null) {
                blogEntriesSyncingService.fetchAndStoreBlogEntries(secretKey)
            }
        }
    }

    fun getDriveToken() {
        if (googleDriveService.authToken == null) {
            googleDriveService.getDriveToken()
        } else {
            Thread {
                googleDriveService
                    .getTokenKey()
                    .map { encryptionService.storeSecretKey(it) }
                    .subscribe({
                        Timber.d("Success")
                    }, {
                        Timber.e(it.cause)
                    })
            }.start()
        }
    }

    /*
    private fun generateStoreAndUploadKey(): Observable<String> {
        val secretKey = encryptionService.generateSecretKey()!!
        val encodedKey = encryptionService.encodeSecretKey(secretKey)
        return googleDriveService.createKeyFile(encodedKey)
            .andThen(Observable.defer { Observable.just(encodedKey) })
    }

     */

}
