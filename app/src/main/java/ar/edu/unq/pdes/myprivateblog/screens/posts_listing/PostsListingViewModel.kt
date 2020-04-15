package ar.edu.unq.pdes.myprivateblog.screens.posts_listing

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import javax.inject.Inject

class PostsListingViewModel @Inject constructor(
    blogEntriesService: BlogEntriesService,
    context: Context
): BaseViewModel(blogEntriesService, context) {

    val posts: LiveData<List<BlogEntry>> by lazy {
        blogEntriesService.getAll()
    }

}
