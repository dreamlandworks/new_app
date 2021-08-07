package com.satrango.ui.auth.user_signup.otp_verification

import android.content.Context
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.models.ForgotPwdOtpReqModel
import com.satrango.ui.auth.user_signup.models.OTPVeriticationModel
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

open class OTPVerificationRepository: BaseRepository() {

    suspend fun forgotPwdRequestOTP(context: Context): ResponseBody {
        val requestBody = ForgotPwdOtpReqModel(UserUtils.getPhoneNo(context), RetrofitBuilder.USER_KEY)
        return RetrofitBuilder.getUserRetrofitInstance().userForgetPwdOtpRequest(requestBody)
    }

    suspend fun requestOTP(context: Context): ResponseBody {
        val requestBody = OTPVeriticationModel(UserUtils.getFirstName(context), UserUtils.getLastName(context), UserUtils.getPhoneNo(context), RetrofitBuilder.USER_KEY)
        return RetrofitBuilder.getUserRetrofitInstance().userRequestOTP(requestBody)
    }

}