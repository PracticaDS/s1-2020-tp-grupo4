package ar.edu.unq.pdes.myprivateblog

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import io.reactivex.disposables.Disposable

abstract class BaseViewModel constructor(
    val blogEntriesService: BlogEntriesService,
    val context: Context
) : ViewModel() {

    val state = MutableLiveData(State.EDITING)
    var post = MutableLiveData<BlogEntry?>()

    enum class State {
        EDITING, POST_EDITED, ERROR, POST_DELETED, POST_CREATED
    }

    fun fetchBlogEntry(id: EntityID) : Disposable {
        return blogEntriesService
            .fetch(id)
            .compose(RxSchedulers.flowableAsync())
            .subscribe {
                post.value = it
            }
    }

}