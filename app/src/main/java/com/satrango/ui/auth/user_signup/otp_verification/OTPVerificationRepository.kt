package com.satrango.ui.auth.user_signup.otp_verification

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.models.ForgetPwdOtpReqModel
import com.satrango.ui.auth.user_signup.models.OTPVeriticationModel
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

open class OTPVerificationRepository: BaseRepository() {

    suspend fun forgotPwdRequestOTP(): ResponseBody {
        val requestBody = ForgetPwdOtpReqModel(UserUtils.phoneNo, RetrofitBuilder.USER_KEY)
        return RetrofitBuilder.getUserRetrofitInstance().userForgetPwdOtpRequest(requestBody)
    }

    suspend fun requestOTP(): ResponseBody {
        val requestBody = OTPVeriticationModel(UserUtils.firstName, UserUtils.lastName, UserUtils.phoneNo, RetrofitBuilder.USER_KEY)
        return RetrofitBuilder.getUserRetrofitInstance().userRequestOTP(requestBody)
    }

}