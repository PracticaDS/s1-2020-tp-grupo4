package ar.edu.unq.pdes.myprivateblog.services.drive

import android.content.Context
import android.util.Log
import ar.edu.unq.pdes.myprivateblog.services.utils.RetrofitServiceBuilder
import ar.edu.unq.pdes.myprivateblog.services.utils.ThreadedTask
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject

class GoogleDriveService @Inject constructor(
    val context: Context
){
    private val googleDriveService = RetrofitServiceBuilder
        .buildService(GoogleDriveApi::class.java)
    private val appDataFolderAsParent = listOf("appDataFolder")
    var authToken: String? = null

    private fun getAuthorizationString(): String {
        return "Bearer $authToken"
    }

    fun getDriveToken() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                authToken = it.result!!.token
            } else {
                Log.e("ERROR", it.exception.toString())
            }
        }
        /*
        val acct = GoogleSignIn.getLastSignedInAccount(context)
        getDriveToken(acct!!)
        */
    }

    fun getDriveToken(acct: GoogleSignInAccount) {
        val task = ThreadedTask<String>()
        task.addOnSuccess {
            authToken = it
        }
        task.addOnFailure {
            Log.d("FAILURE", it)
        }
        task.execute(Executor{}, {
            GoogleAuthUtil.getToken(context, acct.account, "oauth2:https://www.googleapis.com/auth/drive.appdata")
        })
    }

    fun getTokenKey(): Observable<JsonObject> {
        return googleDriveService
            .getFiles(getAuthorizationString())
            .flatMap {
                Log.d("DRIVE", it.toString())
                val keyFileId = it.files[0].asJsonObject.get("id").asString
                return@flatMap googleDriveService.getKeyFile(keyFileId, getAuthorizationString())
            }
    }

    fun createKeyFile(token: String): Completable {
        val file = createFile(token, "key")
        val jsonMetadata = Gson().toJson(RetrofitMetadataPart(appDataFolderAsParent, file.name))
        val metadataPart = MultipartBody.Part.create(
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonMetadata)
        )
        val multimediaPart = MultipartBody.Part.create(
            RequestBody.create(MediaType.parse("plain/text"), file)
        )
        file.delete()
        return googleDriveService.createKeyFile(metadataPart, multimediaPart, getAuthorizationString())

    }
}

private fun createFile(content: String, name: String): File {
    val tempFile = createTempFile(name, "", null)
    tempFile.writeText(content)
    return tempFile
}

data class RetrofitMetadataPart(
    val parents: List<String>, //directories
    val name: String //file name
)