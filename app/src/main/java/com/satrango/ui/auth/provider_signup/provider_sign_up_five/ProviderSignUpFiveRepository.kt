package com.satrango.ui.auth.provider_signup.provider_sign_up_five

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

class ProviderSignUpFiveRepository: BaseRepository() {

    suspend fun uploadVideo(userId: RequestBody, videoNo: RequestBody, key: RequestBody, videoRecord: MultipartBody.Part): ResponseBody {
        return RetrofitBuilder.getServiceProviderRetrofitInstance().providerVideoVerification(userId, videoNo, key, videoRecord)
    }

}