package com.satrango.ui.auth.forgot_password

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import okhttp3.ResponseBody

open class ForgotPwdRepository: BaseRepository() {

    suspend fun verifyUser(requestBody: ForgotPwdVerifyReqModel): ResponseBody {
        return RetrofitBuilder.getUserRetrofitInstance().verifyUser(requestBody)
    }

}