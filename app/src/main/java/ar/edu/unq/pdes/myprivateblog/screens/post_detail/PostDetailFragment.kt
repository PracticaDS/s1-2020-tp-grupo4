package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ar.edu.unq.pdes.myprivateblog.*
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_post_detail.*
import java.io.File


class PostDetailFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_post_detail

    private val viewModel by viewModels<PostDetailViewModel> { viewModelFactory }

    private val args: PostDetailFragmentArgs by navArgs()

    fun goNavActionEditPost(postID: Int) =  findNavController().navigate(PostDetailFragmentDirections.navActionEditPost(postID))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        viewModel.logEvent(args.postId)

        viewModel.fetchBlogEntry(args.postId)

        viewModel.post.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                renderBlogEntry(it)
            }
        })

        viewModel.state.observe(viewLifecycleOwner, Observer {
            when(it){
                BaseViewModel.State.POST_DELETED -> {
                    closeAndGoBack()
                }
            }
        })

        btn_back.setOnClickListener {
            closeAndGoBack()
        }

        btn_edit.setOnClickListener {
            goNavActionEditPost(args.postId)
        }

        btn_delete.setOnClickListener {
            viewModel.delete()
            Snackbar.make(it, R.string.post_deleted_successful, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_action, View.OnClickListener {
                    viewModel.undoDelete()
                }).show();
        }

    }

    fun renderBlogEntry(post: BlogEntry) {
        title.text = post.title

        header_background.setBackgroundColor(post.cardColor)
        applyStatusBarStyle(post.cardColor)
        title.setTextColor(ColorUtils.findTextColorGivenBackgroundColor(post.cardColor))

        body.settings.javaScriptEnabled = true
        body.settings.setAppCacheEnabled(true)
        body.settings.cacheMode = WebSettings.LOAD_DEFAULT
        body.webViewClient = WebViewClient()
        if (post.bodyPath != null && context != null) {
            val content = File(context?.filesDir, post.bodyPath).readText()
            body.loadData(content, "text/html", "UTF-8")
        }
    }
}