package ar.edu.unq.pdes.myprivateblog.services.drive

import com.google.gson.JsonObject
import java.io.Serializable

// Ref: https://developers.google.com/drive/api/v3/reference/files/generateIds
class GenerateIdsResponse(val kind: String, val space: String, val ids: List<String>): Serializable
class GetListResponse(val kind: String, val nextPageToken: String, val incompleteSearch: Boolean, val files: List<JsonObject>): Serializable