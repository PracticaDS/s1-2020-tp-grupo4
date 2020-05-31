package ar.edu.unq.pdes.myprivateblog.data

import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BlogEntriesRepository(private val appDatabase: AppDatabase) {
    fun getActiveBlogEntries() =
        LiveDataReactiveStreams.fromPublisher(appDatabase.blogEntriesDao().getAll(false))

    fun getBlogEntriesWith(deleted: Boolean? = null, synced: Boolean? = null) =
        LiveDataReactiveStreams.fromPublisher(appDatabase.blogEntriesDao().getAll(deleted, synced))

    fun fetchLiveById(entryId: EntityID) =
        LiveDataReactiveStreams.fromPublisher(appDatabase.blogEntriesDao().loadById(entryId))

    fun fetchById(entryId: EntityID) = appDatabase.blogEntriesDao().loadById(entryId)

    fun createBlogEntry(blogEntry: BlogEntry) = appDatabase.blogEntriesDao()
        .insert(blogEntry)
        .subscribeOn(Schedulers.io())

    fun updateBlogEntry(album: BlogEntry) =
        appDatabase.blogEntriesDao()
            .update(album)
            .subscribeOn(Schedulers.io())

    fun getDataCount() : Int {
        return appDatabase.blogEntriesDao().getDataCount(deleted = false)
    }
}