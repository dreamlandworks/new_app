package com.satrango.remote

import com.google.gson.GsonBuilder
import com.satrango.remote.api_services.ServiceProviderApiService
import com.satrango.remote.api_services.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    const val BASE_URL = "http://dev.satrango.com/"
    const val USER_KEY = "BbJOTPWmcOaAJdnvCda74vDFtiJQCSYL"
    const val FCM_URL = "https://fcm.googleapis.com/"
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

    fun getServiceProviderRetrofitInstance(): ServiceProviderApiService {
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
            .build().create(ServiceProviderApiService::class.java)
    }

    fun getFCMRetrofitInstance(): UserApiService {
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
            .baseUrl(FCM_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(UserApiService::class.java)
    }

}