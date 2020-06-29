package ar.edu.unq.pdes.myprivateblog.screens.post_create

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import ar.edu.unq.pdes.myprivateblog.*
import kotlinx.android.synthetic.main.fragment_post_edit.*


class PostCreateFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_post_edit

    private val viewModel by viewModels<PostCreateViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        viewModel.state.observe(viewLifecycleOwner, Observer {
            when (it) {

                BaseViewModel.State.ERROR -> {
                    // TODO: manage error states
                }

                BaseViewModel.State.POST_CREATED -> {
                    findNavController().navigate(
                        PostCreateFragmentDirections.navActionSaveNewPost(
                            viewModel.postId
                        )
                    )
                }

                else -> { /* Do nothing, should not happen*/
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

        title.doOnTextChanged { text, _, _, _ ->
            viewModel.titleText.postValue(text.toString())
        }

        body.doOnTextChanged{ text, _,_,_ ->
            viewModel.bodyText = text.toString()
        }

        btn_save.setOnClickListener {
            viewModel.createPost()
        }

        btn_close.setOnClickListener {
            closeAndGoBack()
        }

        color_picker.onColorSelectionListener = {
            viewModel.cardColor.postValue(it)
        }

        context?.setAztec(body, source, formatting_toolbar)
    }

}