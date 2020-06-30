package ar.edu.unq.pdes.myprivateblog.services.drive

import android.content.Context
import androidx.core.content.edit
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import ar.edu.unq.pdes.myprivateblog.services.googleApi.GoogleApiService
import ar.edu.unq.pdes.myprivateblog.services.utils.RetrofitServiceBuilder
import ar.edu.unq.pdes.myprivateblog.services.utils.ThreadedTask
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject

class GoogleDriveService @Inject constructor(
    val context: Context,
    private val googleApiService: GoogleApiService,
    private val encryptionService: EncryptionService
){
    private val googleDriveService = RetrofitServiceBuilder
        .buildService(GoogleDriveApi::class.java)
    private val appDataFolderAsParent = listOf("appDataFolder")

    private fun getAuthorizationString(): String {
        val accessToken = this.retrieveAccessToken()
        return "Bearer $accessToken"
    }

    private fun getDriveToken(code: ((String) -> Unit)? = null) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val acct = GoogleSignIn.getLastSignedInAccount(context)
                if (acct?.getServerAuthCode() != null) {
                    val refreshToken = this.retrieveRefreshToken()
                    googleApiService.getToken(
                        it.result!!.token!!,
                        refreshToken,
                        acct.getServerAuthCode()!!
                    )
                        .map { response ->
                            this.storeAccessToken(response.access_token)
                            if (response.refresh_token != null) {
                                this.storeRefreshToken(response.refresh_token)
                            }
                            response.access_token
                        }
                        .subscribe { accessToken -> if (code != null) code(accessToken) }
                } else {
                    Completable.complete().toObservable<String>()
                }
            } else {
                Timber.e(it.exception.toString())
                Completable.complete().toObservable<String>()
            }
        }
    }

    private fun getSecretKey(): Observable<String> {
        return googleDriveService
            .getFiles(getAuthorizationString())
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

    private fun createKeyFile(token: String): Completable {
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

    private fun storeAccessToken(accessToken: String) {
        val accessTokenKey = context.getString(R.string.access_token_key)
        this.storeToken(accessTokenKey, accessToken)
    }

    private fun storeRefreshToken(refreshToken: String) {
        val refreshTokenKey = context.getString(R.string.refresh_token_key)
        this.storeToken(refreshTokenKey, refreshToken)
    }

    private fun storeToken(key: String, token: String) {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit {
            this.putString(key, token)
            this.commit()
        }
    }

    private fun retrieveAccessToken(): String? {
        val accessTokenKey = context.getString(R.string.access_token_key)
        return this.retrieveToken(accessTokenKey)
    }

    private fun retrieveRefreshToken(): String? {
        val refreshTokenKey = context.getString(R.string.refresh_token_key)
        return this.retrieveToken(refreshTokenKey)
    }

    private fun retrieveToken(key: String): String? {
        return context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        ).getString(key, null)
    }

    fun fetchAndStoreSecretKey() {
        val secretKey = encryptionService.retrieveSecretKey()
        if (secretKey == null) {
            val accessToken = this.retrieveAccessToken()
            Thread {
                if (accessToken == null) {
                    this.getDriveToken {
                        this.getSecretKey()
                            .subscribe { encodedSecretKey -> encryptionService.storeSecretKey(encodedSecretKey) }
                    }
                } else {
                    this.getSecretKey()
                        .subscribe { encodedSecretKey -> encryptionService.storeSecretKey(encodedSecretKey) }
                }
            }.start()
        }
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