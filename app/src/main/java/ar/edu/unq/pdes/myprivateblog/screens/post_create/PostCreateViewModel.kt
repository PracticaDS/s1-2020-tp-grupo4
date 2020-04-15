package ar.edu.unq.pdes.myprivateblog.screens.post_create

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class PostCreateViewModel @Inject constructor(
    blogEntriesService: BlogEntriesService,
    context: Context
): BaseViewModel(blogEntriesService, context) {

    val titleText = MutableLiveData("")
    val bodyText = MutableLiveData("")
    val cardColor = MutableLiveData(Color.LTGRAY)

    var postId = 0

    fun createPost() : Disposable {
        return blogEntriesService.create(titleText.value!!, bodyText.value!!, cardColor.value!!)
            .compose(RxSchedulers.flowableAsync()).subscribe {
                postId = it.toInt()
                state.value = State.SUCCESS
        }
    }

}
