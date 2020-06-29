package ar.edu.unq.pdes.myprivateblog.screens.sign

import android.content.Context
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import javax.inject.Inject

class OauthSignViewModel @Inject constructor(
    blogEntriesService: BlogEntriesService,
    private val encryptionService: EncryptionService,
    context: Context
): BaseViewModel(blogEntriesService, context){

    fun checkAndCreateSecretKey() {
        if (SYNCING_FEATURE_ENABLED) {
            val secretKey = encryptionService.retrieveSecretKey()
            if (secretKey == null) {
                encryptionService.downloadSecretKey {
                    if (it == null) {
                        val newSecretKey = encryptionService.generateSecretKey()!!
                        encryptionService.uploadSecretKey(newSecretKey)!!
                            .addOnSuccessListener {
                                encryptionService.storeSecretKey(newSecretKey)
                            }
                    } else {
                        encryptionService.storeSecretKey(it)
                    }
                }
            }
        }
    }
}