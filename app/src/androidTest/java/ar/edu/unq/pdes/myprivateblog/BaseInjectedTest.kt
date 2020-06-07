package ar.edu.unq.pdes.myprivateblog

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unq.pdes.myprivateblog.data.AppDatabase
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.services.BlogEntriesService

open class BaseInjectedTest {

    private val context : Context = InstrumentationRegistry.getInstrumentation().targetContext

    protected val blogEntriesRepository : BlogEntriesRepository = BlogEntriesRepository(
        Room.databaseBuilder(
            context,
            AppDatabase::class.java, "myprivateblog.db"
        ).build())

    protected val blogEntriesService : BlogEntriesService = BlogEntriesService(blogEntriesRepository, context)

}