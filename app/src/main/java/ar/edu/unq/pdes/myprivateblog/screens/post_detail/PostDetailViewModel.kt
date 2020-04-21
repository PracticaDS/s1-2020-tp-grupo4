package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PostDetailViewModel @Inject constructor(
    blogEntriesService: BlogEntriesService,
    context: Context
): BaseViewModel(blogEntriesService, context) {


    fun delete() : Disposable {
        return blogEntriesService.logicalDelete(post.value!!)
            .subscribe {
                state.value = State.POST_DELETED
            }
    }

}