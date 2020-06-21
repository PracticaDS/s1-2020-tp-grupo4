package ar.edu.unq.pdes.myprivateblog.services.googleApi

import java.io.Serializable

class GoogleApiResponses(val expires_in: Int, val token_type: String, val refresh_token: String, val id_token: String, val access_token: String): Serializable