package ar.edu.unq.pdes.myprivateblog.screens.post_edit

import android.content.Context
import android.graphics.Color
import android.text.Editable
import androidx.lifecycle.MutableLiveData
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class PostEditViewModel @Inject constructor(
    blogEntriesService: BlogEntriesService,
    context: Context
): BaseViewModel(blogEntriesService, context) {

    var editableTitle = Editable.Factory.getInstance().newEditable("")
    var editableBody = Editable.Factory.getInstance().newEditable("")
    val cardColor = MutableLiveData(Color.LTGRAY)
    var titleText = ""
    var bodyText = ""

    fun editPost() : Disposable{
        val postSecure = post.value!!
        return blogEntriesService.update(postSecure.uid, titleText, postSecure.bodyPath!!, bodyText, cardColor.value!!)
            .compose(RxSchedulers.flowableAsync()).subscribe {
            state.value = State.POST_EDITED
        }
    }
}
