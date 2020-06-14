package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import android.graphics.Color
import android.util.Base64
import ar.edu.unq.pdes.myprivateblog.BaseViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import ar.edu.unq.pdes.myprivateblog.rx.RxSchedulers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.reactivex.rxkotlin.toObservable
import org.threeten.bp.OffsetDateTime
import java.io.File
import java.io.Serializable
import javax.crypto.SecretKey
import javax.inject.Inject

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
                        val userBlogEntrysRef = db
                            .document("users/$userUid")
                            .collection("blogEntries")
                            .document(it.uid.toString())
                        val salt = if (it.salt != null) {
                            it.salt!!
                        } else {
                            it.salt = encryptionService.generateSalt()
                            it.salt!!
                        }
                        val blogEntryForUpload = convertForUploading(it, secretKey, salt)
                        batch.set(
                            userBlogEntrysRef,
                            blogEntryForUpload,
                            SetOptions.merge()
                        )
                    }
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        list
                            .map { blogEntry -> blogEntriesService.update(blogEntry.copy(synced = true, salt = blogEntry.salt)) }
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
                        task.result?.forEach { it ->
                            val blogEntry = it.toObject(BlogEntryFirestore::class.java)
                            val blogEntryBody = if (blogEntry.encryptedBody != null) {
                                val encryptedBodyWithSalt = Base64.decode(blogEntry.encryptedBody!!, Base64.NO_WRAP)
                                val encryptedBodyAndSalt = encryptionService.getSaltAndEncryptedData(encryptedBodyWithSalt)
                                encryptionService.decrypt(
                                    secretKey,
                                    encryptedBodyAndSalt.first,
                                    encryptedBodyAndSalt.second
                                )
                            } else blogEntry.body

                            blogEntriesService.create(
                                blogEntry.title,
                                blogEntryBody ?: "",
                                blogEntry.cardColor!!,
                                blogEntry.uid
                            )
                        }
                    }
                }
        }
    }

    private fun convertForUploading (blogEntry: BlogEntry, secretKey: SecretKey, salt: ByteArray ): BlogEntryFirestore {
        val content = File(context.filesDir, blogEntry.bodyPath!!).readText()
        val encryptedContent = encryptionService.encrypt(secretKey, content, salt)
        val encryptedContentWithSalt = encryptionService.concatWithSalt(salt, encryptedContent)
        // Encoding to String is needed because ByteArray is not serializable
        val encodedEncryptedContentWithSalt = Base64.encodeToString(encryptedContentWithSalt, Base64.NO_WRAP)
        return BlogEntryFirestore(
            blogEntry.uid,
            blogEntry.title,
            encodedEncryptedContentWithSalt,
            blogEntry.imagePath,
            blogEntry.deleted,
            blogEntry.date,
            blogEntry.cardColor
        )
    }
}

private class BlogEntryFirestore(var uid: EntityID? = null,
                                 var title: String = "",
                                 var encryptedBody: String? = null,
                                 var imagePath: String? = "",
                                 var deleted: Boolean = false,
                                 var date: OffsetDateTime? = null,
                                 var cardColor: Int? = Color.WHITE,
                                 var body: String? = null): Serializable