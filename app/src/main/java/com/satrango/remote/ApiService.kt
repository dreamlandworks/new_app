package com.satrango.remote

import com.satrango.ui.auth.models.user_signup.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.*

interface ApiService {

    @POST(UserApiEndPoints.LOGIN)
    suspend fun login(
        @Body json: UserLoginModel
    ): ResponseBody

    @POST(UserApiEndPoints.NEW_USER)
    suspend fun userSignUp(
        @Body json: UserSignUpModel
    ): ResponseBody

    @POST(UserApiEndPoints.OTP_REQUEST)
    suspend fun userRequestOTP(
        @Body json: OTPVeriticationModel
    ): ResponseBody

    @POST(UserApiEndPoints.OTP_REQUEST_FORGOT_PWD)
    suspend fun userForgetPwdOtpRequest(
        @Body json: ForgetPwdOtpReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.USER_RESET_PASSWORD)
    suspend fun userResetPassword(
        @Body json: UserResetPwdModel
    ): ResponseBody


}