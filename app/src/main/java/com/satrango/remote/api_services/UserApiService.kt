package com.satrango.remote.api_services

import com.satrango.remote.end_points.UserApiEndPoints
import com.satrango.ui.auth.forgot_password.ForgotPwdVerifyReqModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_two.models.ProviderSignUpTwoKeywordsResModel
import com.satrango.ui.auth.user_signup.models.*
import com.satrango.ui.user.bookings.bookaddress.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.bookaddress.models.SingleMoveBookingReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.faqs.models.UserFAQResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileUpdateReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.UserKeywordsResModel
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models.UserLocationChangeReqModel
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models.UserLocationChangeResModel
import okhttp3.ResponseBody
import retrofit2.http.*

interface UserApiService {

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
        @Body json: ForgotPwdOtpReqModel
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

    @POST(UserApiEndPoints.USER_SEARCH_RESULTS)
    suspend fun getUserSearchResults(
        @Body map: SearchServiceProviderReqModel
    ): SearchServiceProviderResModel

    @GET(UserApiEndPoints.USER_KEYWORDS)
    suspend fun userKeywords(
        @QueryMap map: Map<String, String>
    ): ProviderSignUpTwoKeywordsResModel

    @GET(UserApiEndPoints.USER_FAQS)
    suspend fun userFAQs(
        @Query("key") key: String
    ): UserFAQResModel

    @POST(UserApiEndPoints.USER_SINGLE_MOVE_BOOKING)
    suspend fun bookSingleMoveProvider(
        @Body json: SingleMoveBookingReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.USER_SINGLE_MOVE_BOOKING)
    suspend fun bookBlueCollarProvider(
        @Body json: BlueCollarBookingReqModel
    ): ResponseBody

}