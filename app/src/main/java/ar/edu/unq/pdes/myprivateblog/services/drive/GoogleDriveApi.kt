package ar.edu.unq.pdes.myprivateblog.services.drive

import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface GoogleDriveApi {

    // Spaces must be "appDataFolder"
    @GET("/drive/v3/files")
    fun getFiles(@Header("Authorization") authToken: String,
                 @Query("q") query: String,
                 @Query("spaces") spaces: String = "appDataFolder",
                 @Query("key") key: String = "AIzaSyDJQytdiTV0F9KcrlRbR-UvjL5KGuWkEAQ"
    ): Observable<GetListResponse>

    // Spaces must be "appDataFolder"
    @GET("/drive/v3/files/{fileId}")
    fun getFile(@Path("fileId") fileId: String,
                @Header("Authorization") authToken: String,
                @Query("spaces") spaces: String = "appDataFolder",
                @Query("key") key: String = "AIzaSyDJQytdiTV0F9KcrlRbR-UvjL5KGuWkEAQ"
    ): Observable<JsonObject>

    // Spaces must be "appDataFolder"
    @GET("/drive/v3/files/generateIds")
    fun getNewFileId(@Header("Authorization") authToken: String,
                     @Query("count") count: Int = 1,
                     @Query("space") spaces: String = "appDataFolder",
                     @Query("key") key: String = "AIzaSyDJQytdiTV0F9KcrlRbR-UvjL5KGuWkEAQ"
    ): Observable<GenerateIdsResponse>

    // Spaces must be "appDataFolder"
    @Multipart
    @Headers("Content-Type: text/plain")
    @POST("/upload/drive/v3/files")
    fun createKeyFile(@Part metadata: MultipartBody.Part,
                      @Part fileMedia: MultipartBody.Part,
                      @Header("Authorization") authToken: String,
                      @Query("spaces") spaces: String = "appDataFolder",
                      @Query("key") key: String = "AIzaSyDJQytdiTV0F9KcrlRbR-UvjL5KGuWkEAQ",
                      @Query("uploadType") type: String = "multipart"
    ): Completable
}