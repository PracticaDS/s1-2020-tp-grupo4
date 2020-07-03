package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import android.graphics.Color
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.reactivex.rxkotlin.toObservable
import org.threeten.bp.OffsetDateTime
import java.io.*
import javax.crypto.SecretKey
import javax.inject.Inject
import android.util.Base64

class BlogEntriesSyncingService @Inject constructor (
    val blogEntriesService: BlogEntriesService,
    private val encryptionService: EncryptionService,
    val context: Context
){
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun uploadUnsyncedBlogEntries(secretKey: SecretKey) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userUid = user.uid
            blogEntriesService.getAllUnsynced().observeForever { list ->
                db.runBatch { batch ->
                    list.forEach {
                        // Upload blog entry to firestore
                        val userBlogEntryRef = db
                            .document("users/$userUid")
                            .collection("blogEntries")
                            .document(it.uid.toString())

                        val encryptedContent = ByteArrayOutputStream()
                        val blogEntryForUpload = convertForUploading(it, secretKey, encryptedContent)
                        encryptedContent.close()

                        blogEntryForUpload.body = Base64.encodeToString(encryptedContent.toByteArray(), Base64.NO_WRAP)

                        batch.set(
                            userBlogEntryRef,
                            blogEntryForUpload,
                            SetOptions.merge()
                        )
                    }
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        list
                            .map { blogEntry -> blogEntriesService.update(blogEntry.copy(synced = true)) }
                            .toObservable()
                            .flatMapCompletable { completables -> completables }
                            .subscribe()
                    }
                }
            }
        }
    }

    fun fetchAndStoreBlogEntries(secretKey: SecretKey) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userUid = user.uid
            db.document("users/$userUid")
                .collection("blogEntries")
                .whereEqualTo("deleted", false)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.forEach { blogEntryDownloaded ->
                            val blogEntry = blogEntryDownloaded.toObject(BlogEntryFirestore::class.java)
                            val blogEntryUid = blogEntry.uid

                            var bodyContent = ""
                            if (blogEntry.body != null) {
                                val encodedEncryptedBody = Base64.decode(blogEntry.body, Base64.NO_WRAP)
                                val encryptedStream = ByteArrayInputStream(encodedEncryptedBody)
                                val decryptedStream = ByteArrayOutputStream()

                                encryptionService.decrypt(secretKey, encryptedStream, decryptedStream)
                                decryptedStream.close()
                                bodyContent = String(decryptedStream.toByteArray(), Charsets.UTF_8)
                            }
                            blogEntriesService.create(
                                blogEntry.title,
                                bodyContent,
                                blogEntry.cardColor!!,
                                blogEntryUid
                            )
                        }
                    }
                }
        }
    }

    private fun convertForUploading (blogEntry: BlogEntry, secretKey: SecretKey, outputStream: OutputStream): BlogEntryFirestore {
        val contentInputStream = File(context.filesDir, blogEntry.bodyPath!!).inputStream()
        encryptionService.encrypt(secretKey, contentInputStream, outputStream)
        return BlogEntryFirestore(
            blogEntry.uid,
            blogEntry.title,
            blogEntry.imagePath,
            blogEntry.deleted,
            blogEntry.date,
            blogEntry.cardColor
        )
    }
}

private class BlogEntryFirestore(var uid: EntityID? = null,
                                 var title: String = "",
                                 var imagePath: String? = "",
                                 var deleted: Boolean = false,
                                 var date: OffsetDateTime? = null,
                                 var cardColor: Int? = Color.WHITE,
                                 var body: String? = null): Serializable