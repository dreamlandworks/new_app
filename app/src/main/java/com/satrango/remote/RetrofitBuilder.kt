package com.satrango.remote

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private const val BASE_URL = "http://dev.satrango.com/api/"

    fun getRetrofitInstance() : ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build().create(ApiService::class.java)
    }

}