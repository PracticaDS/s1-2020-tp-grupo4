package ar.edu.unq.pdes.myprivateblog

import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesSyncingService
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import ar.edu.unq.pdes.myprivateblog.services.drive.GoogleDriveService
import javax.inject.Inject

class MainActivityViewModel  @Inject constructor(
    /* add injectable dependencies here */
    val encryptionService: EncryptionService,
    val blogEntriesSyncingService: BlogEntriesSyncingService,
    val googleDriveService: GoogleDriveService
) : ViewModel() {

}