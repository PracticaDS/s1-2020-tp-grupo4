package ar.edu.unq.pdes.myprivateblog.screens.sign

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import ar.edu.unq.pdes.myprivateblog.services.drive.GoogleDriveService
import javax.inject.Inject

class OauthSignViewModel @Inject constructor(
    blogEntriesService: BlogEntriesService,
    private val googleDriveService: GoogleDriveService,
    context: Context
): BaseViewModel(blogEntriesService, context) {

    fun checkAndCreateSecretKey() {
        if (SYNCING_FEATURE_ENABLED) {
            googleDriveService.fetchAndStoreSecretKey()
        }
    }

}