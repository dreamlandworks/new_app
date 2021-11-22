package com.satrango.remote.api_services

import com.satrango.remote.end_points.ServiceProviderEndPoints
import com.satrango.remote.end_points.UserApiEndPoints
import com.satrango.ui.auth.provider_signup.provider_sign_up_four.models.ProviderSignUpFourReqModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.models.ProviderOneModel
import com.satrango.ui.service_provider.provider_dashboard.ProviderLocationReqModel
import com.satrango.ui.service_provider.provider_dashboard.alerts.models.ProviderAlertsReqModel
import com.satrango.ui.service_provider.provider_dashboard.alerts.models.UpdateAlertsToReadReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMemberShipPlanPaymentReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMemberShipPlanPaymentResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.ProviderMyAccountResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews.models.ProviderReviewResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models.ProviderMyBidsResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderBidEditReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderDeleteBidAttachmentReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderBookingResumeReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.ProviderPauseBookingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.model.ProviderInvoiceResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExpenditureIncurredReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ExtraDemandReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderPostBidReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.models.ProviderPostBidResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderGoalsInstallmentsListResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderPostRequestInstallmentReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.ProviderPostRequestInstallmentResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.review.UserRatingReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.ProviderProfileProfessionResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_skills.UpdateSkillsReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.models.update_tariff.UpdateTariffReqModel
import com.satrango.ui.service_provider.provider_dashboard.models.ProviderOnlineReqModel
import com.satrango.ui.service_provider.provider_dashboard.plans.models.ProviderPlansResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.models.UserFAQResModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModel
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
        @Body requestBody: ProviderSignUpFourReqModel
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
        @Body requestBody: ProviderLocationReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.BOOKING_LIST_WITH_DETAILS)
    suspend fun bookingListWithDetails(
        @Body requestBody: ProviderBookingReqModel
    ): ProviderBookingResModel

    @POST(ServiceProviderEndPoints.EXTRA_DEMAND)
    suspend fun postExtraDemand(
        @Body requestBody: ExtraDemandReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.EXPENDITURE_INCURRED)
    suspend fun expenditureIncurred(
        @Body requestBody: ExpenditureIncurredReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.USER_REVIEW)
    suspend fun userReview(
        @Body requestBody: UserRatingReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.INVOICE)
    suspend fun getInvoice(
        @Body requestBody: ProviderInvoiceReqModel
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

    @POST(ServiceProviderEndPoints.PAUSE_BOOKING)
    suspend fun pauseBooking(
        @Body requestBody: ProviderPauseBookingReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.RESUME_BOOKING)
    suspend fun resumeBooking(
        @Body requestBody: ProviderBookingResumeReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.PROVIDER_JOBS_LIST)
    suspend fun getBidsList(
        @Body requestBody: ProviderBookingReqModel
    ): ProviderMyBidsResModel

    @POST(ServiceProviderEndPoints.PROVIDER_JOB_POSTS_LIST)
    suspend fun getBidJobsList(
        @Body requestBody: ProviderBookingReqModel
    ): ProviderMyBidsResModel

    @POST(ServiceProviderEndPoints.POST_BID)
    suspend fun postBid(
        @Body requestBody: ProviderPostBidReqModel
    ): ProviderPostBidResModel

    @POST(ServiceProviderEndPoints.DELETE_BID_ATTACHMENT)
    suspend fun deleteBidAttachment(
        @Body requestBody: ProviderDeleteBidAttachmentReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.EDIT_BID)
    suspend fun editBid(
        @Body requestBody: ProviderBidEditReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.SP_ALERTS)
    suspend fun getProviderAlerts(
        @Body requestBody: ProviderAlertsReqModel
    ): UserAlertsResModel

    @POST(ServiceProviderEndPoints.PROFESSIONAL_DETAILS)
    suspend fun getProfessionalDetails(
        @Body requestBody: ProviderBookingReqModel
    ): ProviderProfileProfessionResModel

    @POST(ServiceProviderEndPoints.UPDATE_SKILLS)
    suspend fun updateSkills(
        @Body requestBody: UpdateSkillsReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.UPDATE_TARIFF)
    suspend fun updateTariff(
        @Body requestBody: UpdateTariffReqModel
    ): ResponseBody

    @GET(ServiceProviderEndPoints.MY_ACCOUNT)
    suspend fun myAccount(
        @Query("key") key: String,
        @Query("sp_id") sp_id: Int,
    ): ProviderMyAccountResModel

    @GET(ServiceProviderEndPoints.REVIEWS)
    suspend fun reviews(
        @Query("key") key: String,
        @Query("sp_id") sp_id: Int,
    ): ProviderReviewResModel

    @GET(ServiceProviderEndPoints.FAQs)
    suspend fun providerFAQs(
        @Query("key") key: String
    ): UserFAQResModel

    @GET(ServiceProviderEndPoints.SP_PLANS)
    suspend fun getPlans(
        @Query("key") key: String,
        @Query("sp_id") sp_id: Int
    ): ProviderPlansResModel

    @POST(ServiceProviderEndPoints.MEMBERSHIP_PAYMENT)
    suspend fun saveMemberShipPlan(
        @Body requestBody: ProviderMemberShipPlanPaymentReqModel
    ): ProviderMemberShipPlanPaymentResModel

    @POST(ServiceProviderEndPoints.UPDATE_ALERTS_TO_READ)
    suspend fun updateAlertsToRead(
        @Body requestBody: UpdateAlertsToReadReqModel
    ): ResponseBody

    @POST(ServiceProviderEndPoints.UPDATE_SP_ONLINE_STATUS)
    suspend fun updateSpOnlineStatus(
        @Body requestBody: ProviderOnlineReqModel
    ): ResponseBody

}