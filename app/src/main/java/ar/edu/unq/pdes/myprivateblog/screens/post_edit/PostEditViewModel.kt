package ar.edu.unq.pdes.myprivateblog.screens.post_edit

import android.content.Context
import android.graphics.Color
import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.io.OutputStreamWriter
import javax.inject.Inject


class PostEditViewModel @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository,
    val context: Context
) : ViewModel() {

    var post = MutableLiveData<BlogEntry>()
    var editableTitle = Editable.Factory.getInstance().newEditable("")
    var editableBody = Editable.Factory.getInstance().newEditable("")
    val cardColor = MutableLiveData<Int>(Color.LTGRAY)
    var titleText = ""
    var bodyText = ""

    enum class State {
        EDITING, SUCCESS, ERROR
    }

    val state = MutableLiveData(State.EDITING)

    fun fetchBlogEntry(id: EntityID) {

        val disposable : Disposable = blogEntriesRepository
            .fetchById(id)
            .compose(RxSchedulers.flowableAsync())
            .subscribe {
                post.value = it
            }

    }

    fun editPost(){
        val disposable = Flowable.fromCallable {
            val fileName = post.value?.bodyPath

            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))

            outputStreamWriter.use { it.flush(); it.write(bodyText) }

            fileName

        }.flatMapSingle {
            blogEntriesRepository.updateBlogEntry(
                BlogEntry(
                    uid = post.value!!.uid,
                    title = titleText,
                    bodyPath = it,
                    cardColor = cardColor.value!!
                )
            ).toSingle { it }
        }.compose(RxSchedulers.flowableAsync()).subscribe {
            state.value = State.SUCCESS
        }
    }
}
