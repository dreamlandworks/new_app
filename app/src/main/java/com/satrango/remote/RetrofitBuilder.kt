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
//    const val BASE_URL = "http://satrango.com/"
//    const val FCM_URL = "https://fcm.googleapis.com/"
    const val PAYTM_URL = "https://securegw-stage.paytm.in/theia/api/v1/"
    const val USER_KEY = "BbJOTPWmcOaAJdnvCda74vDFtiJQCSYL"
    const val PROVIDER_KEY = "Dld0F54x99UeL8nZkByWC0BwUEi4aF4O"

    fun getUserRetrofitInstance(): UserApiService {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
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

//    fun getFCMRetrofitInstance(): UserApiService {
//        val loggingInterceptor = HttpLoggingInterceptor()
//        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//
//        val client = OkHttpClient.Builder()
//            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
//            .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
//            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
//            .addInterceptor(loggingInterceptor)
//            .build()
//
//        val gson = GsonBuilder()
//            .setLenient()
//            .create()
//
//        return Retrofit.Builder()
//            .baseUrl(FCM_URL)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build().create(UserApiService::class.java)
//    }

    fun getPaytmProcessBuilder(): UserApiService {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(PAYTM_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(UserApiService::class.java)
    }

}