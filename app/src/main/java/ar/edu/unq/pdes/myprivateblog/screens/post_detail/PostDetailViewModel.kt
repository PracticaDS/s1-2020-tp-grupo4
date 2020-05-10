package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.content.Context
import android.os.Bundle
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.disposables.Disposable
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

    fun undoDelete() : Disposable{
        return blogEntriesService.undoLogicalDelete(post.value!!).subscribe()
    }

    // Example Analytics logging event.
    fun logEvent(itemId: Int) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId.toString())
        getAnalyticsInstance().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

}