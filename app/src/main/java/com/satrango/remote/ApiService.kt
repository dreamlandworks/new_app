package com.satrango.remote

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("jlogin")
    suspend fun login(
        @Body json: JSONObject
    ): ResponseBody

}