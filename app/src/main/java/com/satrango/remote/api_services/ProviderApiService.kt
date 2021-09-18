package com.satrango.remote.api_services

import com.satrango.remote.end_points.ServiceProviderEndPoints
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProviderSignUpFourReqModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.ProviderLocationReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.ProviderBookingResModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceResModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.release_goals.models.ProviderGoalsInstallmentsListResModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.release_goals.models.ProviderPostRequestInstallmentReqModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.release_goals.models.ProviderPostRequestInstallmentResModel
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.review.UserRatingReqModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ProviderApiService {

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

    @POST(ServiceProviderEndPoints.BOOKING_LIST_WITH_DETAILS)
    suspend fun bookingListWithDetails(
        @Body json: ProviderBookingReqModel
    ): ProviderBookingResModel

    @POST(ServiceProviderEndPoints.EXTRA_DEMAND)
    suspend fun postExtraDemand(
        @Body json: ExtraDemandReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.EXPENDITURE_INCURRED)
    suspend fun expenditureIncurred(
        @Body json: ExpenditureIncurredReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.USER_REVIEW)
    suspend fun userReview(
        @Body json: UserRatingReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.INVOICE)
    suspend fun getInvoice(
        @Body json: ProviderInvoiceReqModel
    ): ProviderInvoiceResModel

    @GET(ServiceProviderEndPoints.GOALS_INSTALLMENTS_LIST)
    suspend fun getGoalsInstallmentsList(
        @Query("key") key: String,
        @Query("post_job_id") postJobId: Int
    ): ProviderGoalsInstallmentsListResModel

    @POST(ServiceProviderEndPoints.POST_REQUEST_INSTALLMENT)
    suspend fun postRequestInstallment(
        @Body requestBody: ProviderPostRequestInstallmentReqModel
    ): ProviderPostRequestInstallmentResModel

}