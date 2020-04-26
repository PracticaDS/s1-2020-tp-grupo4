package ar.edu.unq.pdes.myprivateblog.screens.post_detail

import android.hardware.camera2.TotalCaptureResult
import android.os.Bundle
import android.text.Layout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ar.edu.unq.pdes.myprivateblog.*
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import kotlinx.android.synthetic.main.fragment_post_detail.*
import kotlinx.android.synthetic.main.fragment_post_detail.body
import kotlinx.android.synthetic.main.fragment_post_detail.header_background
import kotlinx.android.synthetic.main.fragment_post_detail.title
import java.io.File

class PostDetailFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_post_detail

    private val viewModel by viewModels<PostDetailViewModel> { viewModelFactory }

    private val args: PostDetailFragmentArgs by navArgs()

    fun goNavActionEditPost(postID: Int) =  findNavController().navigate(PostDetailFragmentDirections.navActionEditPost(postID))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            val deleteView : View = layoutInflater.inflate(R.layout.popup_post_delete, null)
            val window = PopupWindow(deleteView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, false)
            window.isOutsideTouchable = false
            window.showAtLocation(deleteView, Gravity.CENTER, 0, 0);

            val confirmButton = deleteView.findViewById(R.id.btn_delete_confirm) as Button
            confirmButton.setOnClickListener {
                window.dismiss()
                viewModel.delete()
            }
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