package ar.edu.unq.pdes.myprivateblog.services.drive

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import ar.edu.unq.pdes.myprivateblog.services.googleApi.GoogleApiService
import ar.edu.unq.pdes.myprivateblog.services.utils.RetrofitServiceBuilder
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class GoogleDriveService @Inject constructor(
    val context: Context,
    private val googleApiService: GoogleApiService,
    private val encryptionService: EncryptionService
){
    private val googleDriveService = RetrofitServiceBuilder
        .buildService(GoogleDriveApi::class.java)
    private val appDataFolderAsParent = listOf("appDataFolder")
    var authToken: String? = null

    private fun getAuthorizationString(): String {
        return "Bearer $authToken"
    }

    private fun parameterForFileName(fileName: String) = "name = '$fileName'"

    fun getDriveToken() {
        if (this.authToken == null) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.getIdToken(true)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val acct = GoogleSignIn.getLastSignedInAccount(context)
                    if (acct?.getServerAuthCode() != null) {
                        Thread {
                            googleApiService.getToken(
                                it.result!!.token!!,
                                acct.getServerAuthCode()!!
                            )
                                .subscribe(
                                    { response ->
                                        authToken = response.access_token
                                    },
                                    { error ->
                                        Timber.e(error)
                                    }
                                )
                        }.start()
                    }
                } else {
                    Timber.e(it.exception.toString())
                }
            }
        }
    }

    fun getTokenKey(): Observable<String> {
        val parameterForFileName = parameterForFileName(context.getString(R.string.secret_key_drive_file))
        return googleDriveService
            .getFiles(getAuthorizationString())//, parameterForFileName)
            .flatMap {
                Timber.d(it.toString())
                if (it.files.isNotEmpty()) {
                    val keyFileId = it.files[0].asJsonObject.get("id").asString
                    return@flatMap googleDriveService.getFileDownloadUrl(keyFileId, getAuthorizationString())
                } else {
                    val secretKey = encryptionService.generateSecretKey()!!
                    val encodedSecretKey = encryptionService.encodeSecretKey(secretKey)
                    return@flatMap this.createKeyFile(encodedSecretKey)
                        .toObservable<Unit>()
                        .flatMap { Observable.just(encodedSecretKey) }
                }
            }
    }

    fun createKeyFile(token: String): Completable {
        val fileName = context.getString(R.string.secret_key_drive_file)
        val file = createFile(token, fileName)
        val jsonMetadata = Gson().toJson(RetrofitMetadataPart(appDataFolderAsParent, "$fileName.txt", "plain/text"))
        val metadataPart = MultipartBody.Part.create(
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonMetadata)
        )

        val multimediaPart = MultipartBody.Part.create(
            RequestBody.create(MediaType.parse("plain/text"), file)
        )

        return googleDriveService.createKeyFile(metadataPart, multimediaPart, getAuthorizationString())
            .andThen { file.delete() }
    }
}

private fun createFile(content: String, name: String): File {
    val tempFile = createTempFile(name, ".txt", null)
    tempFile.writeText(content)
    return tempFile
}

data class RetrofitMetadataPart(
    val parents: List<String>, //directories
    val name: String, //file name
    val mimeType: String // The file type
)