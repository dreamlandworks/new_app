package com.satrango.remote

import com.satrango.ui.auth.models.user_signup.OTPVeriticationModel
import com.satrango.ui.auth.models.user_signup.UserLoginModel
import com.satrango.ui.auth.models.user_signup.UserSignUpModel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.*

interface ApiService {

    @POST(UserApiEndPoints.LOGIN)
    suspend fun login(
        @Body json: UserLoginModel
    ): ResponseBody

    @Headers("Accept: application/json; charset=UTF-8")
    @POST(UserApiEndPoints.NEW_USER)
    suspend fun userSignUp(
        @Body json: UserSignUpModel
    ): ResponseBody

    @POST(UserApiEndPoints.OTP_REQUEST)
    suspend fun requestOTP(
        @Body json: OTPVeriticationModel
    ): ResponseBody
}