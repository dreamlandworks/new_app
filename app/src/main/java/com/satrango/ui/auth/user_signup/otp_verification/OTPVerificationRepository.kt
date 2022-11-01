package com.satrango.ui.auth.user_signup.otp_verification

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.models.ForgotPwdOtpReqModel
import com.satrango.ui.auth.user_signup.models.OTPVerificationModel
import com.satrango.utils.UserUtils
import okhttp3.ResponseBody

open class OTPVerificationRepository: BaseRepository() {

    suspend fun forgotPwdRequestOTP(context: Context): ResponseBody {
        val requestBody = ForgotPwdOtpReqModel(UserUtils.getPhoneNo(context), RetrofitBuilder.USER_KEY)
        Log.e("FORGOT REQUEST:", Gson().toJson(requestBody))
//        Toast.makeText(context, Gson().toJson(requestBody), Toast.LENGTH_SHORT).show()
        return RetrofitBuilder.getUserRetrofitInstance().userForgetPwdOtpRequest(requestBody)
    }

    suspend fun requestOTP(context: Context): ResponseBody {
        val requestBody = OTPVerificationModel(UserUtils.getFirstName(context), UserUtils.getLastName(context), UserUtils.getPhoneNo(context), RetrofitBuilder.USER_KEY)
        Log.e("OTP REQUEST:", Gson().toJson(requestBody))
//        Toast.makeText(context, Gson().toJson(requestBody), Toast.LENGTH_SHORT).show()
        return RetrofitBuilder.getUserRetrofitInstance().userRequestOTP(requestBody)
    }

}