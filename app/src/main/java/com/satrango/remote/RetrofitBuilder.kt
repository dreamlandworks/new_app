package com.satrango.remote

import com.google.gson.GsonBuilder
import com.satrango.remote.api_services.ProviderApiService
import com.satrango.remote.api_services.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

//    const val BASE_URL = "http://dev.satrango.com/"
    const val BASE_URL = "https://www.squill.in/"
//    const val BASE_URL = "https://www.satrango.com"
//    const val BASE_URL = "http://satrango.com/"
//    const val FCM_URL = "https://fcm.googleapis.com/"
//    const val PAYTM_URL = "https://securegw-stage.paytm.in/theia/api/v1/"
    const val USER_KEY = "L9c4rxAzCfpT3JX08RHaKo5kewSWV6i2"
    const val PROVIDER_KEY = "Mkuxb3rL24pRnyKwvCZilA95IOoJ8VHG"

    fun getUserRetrofitInstance(): UserApiService {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS

        val client = OkHttpClient.Builder()
            .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(UserApiService::class.java)
    }

    fun getServiceProviderRetrofitInstance(): ProviderApiService {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS

        val client = OkHttpClient.Builder()
            .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ProviderApiService::class.java)
    }

}