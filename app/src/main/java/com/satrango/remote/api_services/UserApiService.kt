package com.satrango.remote.api_services

import com.satrango.remote.end_points.UserApiEndPoints
import com.satrango.remote.fcm.FCMMessageReqModel
import com.satrango.ui.auth.FCMReqModel
import com.satrango.ui.auth.forgot_password.ForgotPwdVerifyReqModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_two.models.ProviderSignUpTwoKeywordsResModel
import com.satrango.ui.auth.user_signup.models.*
import com.satrango.ui.user.bookings.provider_response.PaymentConfirmReqModel
import com.satrango.ui.user.bookings.booking_address.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.booking_address.models.SingleMoveBookingReqModel
import com.satrango.ui.user.bookings.booking_attachments.models.MultiMoveReqModel
import com.satrango.ui.user.bookings.change_address.AddBookingAddressReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.ProviderResponseReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models.TransactionHistoryResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.models.UserFAQResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileUpdateReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.UserKeywordsResModel
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models.AllLocationsResModel
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

    @POST(UserApiEndPoints.USER_BLUE_COLLAR_BOOKING)
    suspend fun bookBlueCollarProvider(
        @Body json: BlueCollarBookingReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.USER_MULTI_MOVE_BOOKING)
    suspend fun bookMultiMoveProvider(
        @Body json: MultiMoveReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.USER_ADD_BOOKING_ADDRESS)
    suspend fun addBookingAddress(
        @Body json: AddBookingAddressReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.UPDATE_FCM_TOKEN)
    suspend fun updateFCMToken(
        @Body json: FCMReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.FCM_SEND)
    suspend fun sendFCM(
        @HeaderMap map: Map<String, String>,
        @Body requestBody: FCMMessageReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.CONFIRM_PAYMENT)
    suspend fun confirmPayment(
        @Body requestBody: PaymentConfirmReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.USER_DETAILS_ACCEPT)
    suspend fun getUserBookingDetails(
        @Body json: BookingDetailsReqModel
    ): BookingDetailsResModel

    @POST(UserApiEndPoints.PROVIDER_RESPONSE)
    suspend fun setProviderResponse(
        @Body json: ProviderResponseReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.USER_BOOKING_DETAILS)
    suspend fun getMyBookingDetails(
        @Body json: MyBookingsReqModel
    ): MyBookingsResModel

    @GET(UserApiEndPoints.AUTO_COMPLETE_ADDRESS)
    suspend fun getAllLocations(
        @Query("key") key: String
    ): AllLocationsResModel

    @GET(UserApiEndPoints.TRANSACTiON_HISTORY)
    suspend fun getTransactionHistory(
        @Query("key") key: String,
        @Query("users_id") user_id: String
    ): TransactionHistoryResModel

}