package ar.edu.unq.pdes.myprivateblog.screens.post_edit

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import ar.edu.unq.pdes.myprivateblog.*
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import kotlinx.android.synthetic.main.fragment_post_edit.*
import kotlinx.android.synthetic.main.fragment_post_edit.body
import kotlinx.android.synthetic.main.fragment_post_edit.title
import java.io.File

class PostEditFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_post_edit

    private val viewModel by viewModels<PostEditViewModel> { viewModelFactory }

    private val args : PostEditFragmentArgs by navArgs()

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
                BaseViewModel.State.SUCCESS -> {
                     closeAndGoBack()
                }
            }
        })

        viewModel.cardColor.observe(viewLifecycleOwner, Observer {
            header_background.setBackgroundColor(it)
            val itemsColor = ColorUtils.findTextColorGivenBackgroundColor(it)
            title.setTextColor(itemsColor)
            title.setHintTextColor(itemsColor)
            btn_save.setColorFilter(itemsColor)
            btn_close.setColorFilter(itemsColor)

            applyStatusBarStyle(it)
        })

        title.doOnTextChanged { text,_,_,_->
            viewModel.titleText = text.toString()
        }

        body.doOnTextChanged{ text, _,_,_ ->
            viewModel.bodyText = text.toString()
        }

        color_picker.onColorSelectionListener = {
            viewModel.cardColor.postValue(it)
        }

        btn_close.setOnClickListener {
            closeAndGoBack()
        }

        btn_save.setOnClickListener {
            viewModel.post.value.let {
                it!!.title = viewModel.titleText
            }
            viewModel.editPost()
        }

        context?.setAztec(body, source, formatting_toolbar)
    }

    fun renderBlogEntry(post: BlogEntry) {
        viewModel.editableTitle.append(post.title)
        viewModel.editableBody.append(post.bodyPath)
        viewModel.cardColor.value = post.cardColor
        title.text = viewModel.editableTitle
        header_background.setBackgroundColor(post.cardColor)
        applyStatusBarStyle(post.cardColor)
        title.setTextColor(ColorUtils.findTextColorGivenBackgroundColor(post.cardColor))
        if (post.bodyPath != null && context != null) {
            val content = File(context?.filesDir, post.bodyPath).readText()
            body.setText(content)
        }
    }
}