package ar.edu.unq.pdes.myprivateblog.services.googleApi

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.services.utils.RetrofitServiceBuilder
import io.reactivex.Observable
import javax.inject.Inject

class GoogleApiService @Inject constructor(
    val context: Context
) {

    private val googleApiService = RetrofitServiceBuilder
        .buildService(GoogleApi::class.java)

    fun getToken(idToken: String, refreshToken: String?, deviceCode: String): Observable<GoogleApiResponses> {
        return googleApiService.getToken(
            idToken,
            refreshToken,
            deviceCode,
            context.getString(R.string.web_client_id)
        )
    }

}