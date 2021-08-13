package com.satrango.remote.api_services

import com.satrango.remote.end_points.ServiceProviderEndPoints
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProviderSignUpFourReqModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderLocationReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ServiceProviderApiService {

    @GET(ServiceProviderEndPoints.PROFESSIONS_LIST)
    suspend fun providerList(
        @Query("key") key: String
    ): ProviderOneModel

    @POST(ServiceProviderEndPoints.ACTIVATION_CONFIRMATION)
    suspend fun providerActivationConfirmation(
        @Body json: ProviderSignUpFourReqModel
    ): ResponseBody

    @Multipart
    @POST(ServiceProviderEndPoints.VIDEO_VERIFICATION)
    suspend fun providerVideoVerification(
        @Part("users_id") userId: RequestBody,
        @Part("video_no") video_no: RequestBody,
        @Part("key") key: RequestBody,
        @Part video_record: MultipartBody.Part
    ): ResponseBody

    @POST(ServiceProviderEndPoints.PROVIDER_LOCATION)
    suspend fun saveProviderLocation(
        @Body json: ProviderLocationReqModel
    ): ResponseBody

}