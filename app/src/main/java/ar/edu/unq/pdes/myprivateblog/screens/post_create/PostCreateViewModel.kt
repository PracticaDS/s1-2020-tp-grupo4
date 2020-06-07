package ar.edu.unq.pdes.myprivateblog.screens.post_create

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesSyncingService
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class PostCreateViewModel @Inject constructor(
    blogEntriesService: BlogEntriesService,
    val encryptionService: EncryptionService,
    val blogEntriesSyncingService: BlogEntriesSyncingService,
    context: Context
): BaseViewModel(blogEntriesService, context) {

    val titleText = MutableLiveData("")
    var bodyText = ""
    val cardColor = MutableLiveData(Color.LTGRAY)

    var postId = 0

    fun createPost() : Disposable {
        return blogEntriesService.create(titleText.value!!, bodyText, cardColor.value!!)
            .compose(RxSchedulers.flowableAsync()).subscribe {
                postId = it.toInt()
                state.value = State.POST_CREATED
                sync()
        }
    }

    private fun sync() {
        if (SYNCING_FEATURE_ENABLED) {
            val secretKey = encryptionService.retrieveSecretKey()
            if (secretKey != null) {
                blogEntriesSyncingService.uploadUnsyncedBlogEntries(secretKey)
            }
        }
    }

}
