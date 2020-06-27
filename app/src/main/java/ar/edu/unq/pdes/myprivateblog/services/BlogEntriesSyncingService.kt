package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import android.graphics.Color
import android.util.Log
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.reactivex.rxkotlin.toObservable
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import java.io.*
import javax.crypto.SecretKey
import javax.inject.Inject

class BlogEntriesSyncingService @Inject constructor (
    val blogEntriesService: BlogEntriesService,
    private val encryptionService: EncryptionService,
    val context: Context,
    val db:FirebaseFirestore,
    val storage: FirebaseStorage
){

    fun uploadUnsyncedBlogEntries(secretKey: SecretKey) {
        Log.i("SYNC","entre 1")
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
                        val encryptedContent = PipedOutputStream()
                        val encryptedContentInputStream = PipedInputStream()
                        encryptedContentInputStream.connect(encryptedContent)
                        val blogEntryForUpload = convertForUploading(it, secretKey, encryptedContent)
                        encryptedContent.close()

                        batch.set(
                            userBlogEntryRef,
                            blogEntryForUpload,
                            SetOptions.merge()
                        )

                        // Upload blog entry to storage
                        val userBlogEntryStorageRef = storage
                            .reference
                            .child("users/$userUid/blogEntries")
                            .child(it.uid.toString())

                        userBlogEntryStorageRef.putStream(encryptedContentInputStream)
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
        Log.i("SYNC","entre 2")
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
                            val blogEntryUid = blogEntry.uid

                            val blogEntryStorageRef = storage
                                .reference
                                .child("users/$userUid/blogEntries")
                                .child(blogEntryUid.toString())

                            blogEntryStorageRef.getStream { taskSnapshot, inputStream ->
                                val decryptedContentOutputStream = ByteArrayOutputStream()
                                encryptionService.decrypt(secretKey, inputStream, decryptedContentOutputStream)
                                decryptedContentOutputStream.close()

                                val decryptedContent = String(decryptedContentOutputStream.toByteArray(), Charsets.UTF_8)
                                blogEntriesService.create(
                                    blogEntry.title,
                                    decryptedContent,
                                    blogEntry.cardColor!!,
                                    blogEntryUid
                                )
                            }.addOnFailureListener {
                                Timber.e("Could not find content file for user $userUid and file id $blogEntryUid")
                            }
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