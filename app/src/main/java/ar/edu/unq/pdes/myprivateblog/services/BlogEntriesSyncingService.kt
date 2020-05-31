package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import android.graphics.Color
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.threeten.bp.OffsetDateTime
import java.io.File
import java.io.Serializable
import javax.inject.Inject

class BlogEntriesSyncingService @Inject constructor (
    val blogEntriesService: BlogEntriesService,
    val context: Context
){
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun uploadUnsyncedBlogEntries() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userUid = user.uid
            blogEntriesService.getAllUnsynced().observeForever { list ->
                db.runBatch { batch ->
                    list.forEach {
                        val userBlogEntrysRef = db
                            .document("users/$userUid")
                            .collection("blogEntries")
                            .document(it.uid.toString())
                        batch.set(
                            userBlogEntrysRef,
                            convertForUploading(it),
                            SetOptions.merge()
                        )
                    }
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        list.forEach { blogEntriesService.update(it.copy(synced = true)) }
                    }
                }
            }
        }
    }

    fun fetchAndStoreBlogEntries() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userUid = user.uid
            db.document("users/$userUid")
                .collection("blogEntries")
                .whereEqualTo("deleted", false)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.forEach { it ->
                            val blogEntry = it.toObject(BlogEntryFirestore::class.java)
                            blogEntriesService.create(
                                blogEntry.title!!,
                                blogEntry.body!!,
                                blogEntry.cardColor!!,
                                blogEntry.uid
                            )
                        }
                    }
                }
        }
    }

    private fun convertForUploading (blogEntry: BlogEntry): BlogEntryFirestore {
        val content = File(context?.filesDir, blogEntry.bodyPath).readText()
        return BlogEntryFirestore(
            blogEntry.uid,
            blogEntry.title,
            content,
            blogEntry.imagePath,
            blogEntry.deleted,
            blogEntry.date,
            blogEntry.cardColor
        )
    }
}

private class BlogEntryFirestore(var uid: EntityID? = null,
                                 var title: String? = "",
                                 var body: String? = "",
                                 var imagePath: String? = "",
                                 var deleted: Boolean? = false,
                                 var date: OffsetDateTime? = null,
                                 var cardColor: Int? = Color.WHITE): Serializable