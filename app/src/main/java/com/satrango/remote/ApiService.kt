package com.satrango.remote

import com.satrango.ui.auth.forgot_password.ForgotPwdVerifyReqModel
import com.satrango.ui.auth.user_signup.models.*
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileUpdateReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.UserKeywordsResModel
import okhttp3.ResponseBody
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

    @GET(UserApiEndPoints.USER_BROWSE_CATEGORIES)
    suspend fun userBrowseCategories(
        @Query("key") key: String
    ): ResponseBody

    @POST(UserApiEndPoints.USER_BROWSE_SUB_CATEGORIES)
    suspend fun userBrowseSubCategories(
        @Body map: BrowseCategoryReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.SHOW_USER_PROFILE)
    suspend fun getUserProfile(
        @Body map: BrowseCategoryReqModel
    ): UserProfileResModel

    @POST(UserApiEndPoints.USER_PROFILE_UPDATE)
    suspend fun updateUserProfile(
        @Body map: UserProfileUpdateReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.DELETE_USER_ADDRESS)
    suspend fun deleteUserAddress(
        @Body map: BrowseCategoryReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.USER_GET_ALERTS)
    suspend fun getUserAlerts(
        @Body map: UserAlertsReqModel
    ): UserAlertsResModel

    @GET(UserApiEndPoints.USER_SEARCH_KEYWORDS)
    suspend fun getUserSearchKeywords(
        @Query("key") key: String
    ): UserKeywordsResModel

    @POST(UserApiEndPoints.VERIFY_USER)
    suspend fun verifyUser(
        @Body map: ForgotPwdVerifyReqModel
    ): ResponseBody

//    @POST(UserApiEndPoints.VERIFY_USER)
//    suspend fun verifyUser(
//        @Body map: ForgotPwdVerifyReqModel
//    ): ResponseBody

}