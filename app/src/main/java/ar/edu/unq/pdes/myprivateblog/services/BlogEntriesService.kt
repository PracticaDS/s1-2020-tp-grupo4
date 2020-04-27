package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import androidx.lifecycle.LiveData
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import io.reactivex.Completable
import io.reactivex.Flowable
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
        return blogEntriesRepository.getActiveBlogEntries()
    }

    fun getDataCount() : Int {
        return blogEntriesRepository.getDataCount()
    }

    fun create(title : String, bodyText : String, cardColor : Int) : Flowable<Long> {
        val fileName = UUID.randomUUID().toString() + ".body"
        return writeBody(fileName, bodyText).flatMapSingle {
            blogEntriesRepository.createBlogEntry(
                BlogEntry(
                    title = title,
                    bodyPath = it,
                    cardColor = cardColor
                )
            )
        }
    }

    fun update(blogEntry: BlogEntry): Completable {
        return blogEntriesRepository.updateBlogEntry(
            blogEntry
        ).observeOn(AndroidSchedulers.mainThread())
    }

    fun writeBody(bodyPath: String, bodyText: String): Flowable<String> {
        return Flowable.fromCallable {
            OutputStreamWriter(context.openFileOutput(bodyPath, Context.MODE_PRIVATE)).use {
                it.write(bodyText)
            }
            bodyPath
        }
    }

    fun changeLogicalDelete(blogEntry: BlogEntry, delete: Boolean) : Completable =
        blogEntriesRepository.updateBlogEntry(blogEntry.copy( deleted = delete)).observeOn(AndroidSchedulers.mainThread())

    fun logicalDelete(blogEntry: BlogEntry) : Completable = changeLogicalDelete(blogEntry, true)
    fun undoLogicalDelete(blogEntry: BlogEntry) : Completable = changeLogicalDelete(blogEntry, false)

}
