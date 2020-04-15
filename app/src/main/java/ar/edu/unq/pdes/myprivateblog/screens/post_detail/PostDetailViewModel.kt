package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService
import javax.inject.Inject

class PostDetailViewModel @Inject constructor(
    blogEntriesService: BlogEntriesService,
    context: Context
): BaseViewModel(blogEntriesService, context) {


}