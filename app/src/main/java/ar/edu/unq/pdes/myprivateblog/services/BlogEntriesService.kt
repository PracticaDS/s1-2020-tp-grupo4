package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import androidx.lifecycle.LiveData
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.OutputStreamWriter
import java.util.*
import javax.inject.Inject

class BlogEntriesService @Inject constructor(
    private val blogEntriesRepository: BlogEntriesRepository,
    val context: Context
){
    fun fetch(id: Int) : Flowable<BlogEntry> {
        return blogEntriesRepository
            .fetchById(id)
    }

    fun getAll(): LiveData<List<BlogEntry>>{
        return blogEntriesRepository.getActivesBlogEntries()
    }

    fun create(title : String, bodyText : String, cardColor : Int) : Flowable<Long> {
        return Flowable.fromCallable {
            val fileName = UUID.randomUUID().toString() + ".body"
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.use { it.write(bodyText) }
            fileName
        }.flatMapSingle {
            blogEntriesRepository.createBlogEntry(
                BlogEntry(
                    title = title,
                    bodyPath = it,
                    cardColor = cardColor
                )
            )
        }
    }

    fun update(uid: EntityID, titleText: String, bodyPath: String, bodyText: String, cardColor: Int) : Flowable<String> {
        return Flowable.fromCallable {
            OutputStreamWriter(context.openFileOutput(bodyPath, Context.MODE_PRIVATE)).use{
                it.write(bodyText)
            }
            bodyPath
        }.flatMapSingle {
            blogEntriesRepository.updateBlogEntry(
                BlogEntry(
                    uid = uid,
                    title = titleText,
                    bodyPath = it,
                    cardColor = cardColor
                )
            ).toSingle {
                it
            }.observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun logicalDelete(blogEntry: BlogEntry) : Completable = blogEntriesRepository.updateBlogEntry(blogEntry.copy( deleted = true)).observeOn(
        AndroidSchedulers.mainThread())

}