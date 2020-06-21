package ar.edu.unq.pdes.myprivateblog.services.googleApi

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GoogleApi {

    @FormUrlEncoded
    @POST("oauth2/v4/token")
    fun getToken(@Field("id_token") idTokenString: String,
                 @Field("code") deviceCode: String,
                 @Field("client_id") clientId: String,
                 @Field("client_secret") clientSecret: String = "tlYH9Y9kbHevnucVKWLMGG8g",
                 @Field("redirect_uri") redirectUri: String = "",
                 @Field("grant_type") authorization_code: String = "authorization_code"
    ): Observable<GoogleApiResponses>

}