package com.satrango.remote.api_services

import com.satrango.remote.end_points.UserApiEndPoints
import com.satrango.remote.fcm.models.*
import com.satrango.ui.auth.FCMReqModel
import com.satrango.ui.auth.forgot_password.ForgotPwdVerifyReqModel
import com.satrango.ui.auth.login_screen.LogoutReqModel
import com.satrango.ui.auth.provider_signup.provider_sign_up_two.models.ProviderSignUpTwoKeywordsResModel
import com.satrango.ui.auth.user_signup.models.*
import com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.models.CitiesResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.AddFundsReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.models.AddFundsResModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ChangeExtraDemandStatusReqModel
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.GetBookingStatusListResModel
import com.satrango.ui.user.bookings.booking_address.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.booking_address.models.GetTxnReqModel
import com.satrango.ui.user.bookings.booking_address.models.GetTxnResModel
import com.satrango.ui.user.bookings.booking_address.models.SingleMoveBookingReqModel
import com.satrango.ui.user.bookings.booking_attachments.models.MultiMoveReqModel
import com.satrango.ui.user.bookings.cancel_booking.models.UserBookingCancelReqModel
import com.satrango.ui.user.bookings.change_address.AddBookingAddressReqModel
import com.satrango.ui.user.bookings.payment_screen.models.*
import com.satrango.ui.user.bookings.provider_response.PaymentConfirmReqModel
import com.satrango.ui.user.bookings.raise_ticket.models.RaiseTicketReqModel
import com.satrango.ui.user.bookings.raise_ticket.models.RaiseTicketResModel
import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.GoalsInstallmentsResModel
import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.PostApproveRejectReqModel
import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.PostApproveRejectResModel
import com.satrango.ui.user.bookings.view_booking_details.models.*
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer.models.*
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.models.MyAccountDetailsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.models.TransactionHistoryResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.MyBookingsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models.MyJobPostReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models.MyJobPostResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.AttachmentDeleteReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.AttachmentDeleteResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.MyPostJobEditResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.blue_collar.MyJobPostEditBlueCollarReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.multi_move.MyJobPostMultiMoveEditReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.single_move.MyJobPostSingleMoveEditReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models.*
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.installment_payments.InstallmentPaymentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.installment_payments.InstallmentPaymentResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments.SaveInstallmentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments.SaveInstallmentResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.setgoals.SetGoalsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.RejectJobPostStatusReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.ViewBidsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.ViewBidsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileUpdateReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.PostJobSkillsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.UserBidRangesResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_blue_collar.PostJobBlueCollarReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_blue_collar.PostJobBlueCollarResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_multi_move.PostJobMultiMoveReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_multi_move.PostJobMultiMoveResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.PostJobSingleMoveReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.PostJobSingleMoveResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.*
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models.ComplaintModuleResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models.ComplaintReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.models.ComplaintResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.models.UserFAQResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models.FeedbackReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.models.FeedbackResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests.models.ComplaintRequestResModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderReqModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsReqModel
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserAlertsResModelX
import com.satrango.ui.user.user_dashboard.user_alerts.models.UserUpdateAlertsToReadReqModel
import com.satrango.ui.user.user_dashboard.user_home_screen.models.UserKeywordsResModel
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.models.AllLocationsResModel
import com.satrango.ui.user.user_dashboard.user_offers.models.OffersListReqModel
import com.satrango.ui.user.user_dashboard.user_offers.models.OffersListResModel
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
        @Body json: OTPVerificationModel
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
        @Body map: UserProfileReqModel
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
    ): UserAlertsResModelX

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

    @GET(UserApiEndPoints.TRANSACTION_HISTORY)
    suspend fun getTransactionHistory(
        @Query("key") key: String,
        @Query("users_id") user_id: String
    ): TransactionHistoryResModel

    @GET(UserApiEndPoints.SKILLS)
    suspend fun getSkills(
        @Query("key") key: String
    ): PostJobSkillsResModel

    @GET(UserApiEndPoints.AUTOCOMPLETE_BY_CATEGORY)
    suspend fun getSkillsByCategory(
        @Query("key") key: String,
        @Query("category_id") categoryId: String
    ): PostJobSkillsResModel

    @GET(UserApiEndPoints.PLANS)
    suspend fun getUserPlans(
        @Query("key") key: String,
        @Query("users_id") user_id: Int
    ): PostJobPlansResModel

    @POST(UserApiEndPoints.USER_PLAN_PAYMENT)
    suspend fun saveUserPaymentPlan(
        @Body requestBody: UserPlanPaymentReqModel
    ): UserPlanPaymentResModel

    @GET(UserApiEndPoints.BID_RANGE)
    suspend fun userBidRanges(
        @Query("key") key: String
    ): UserBidRangesResModel

    @POST(UserApiEndPoints.POST_JOB_SINGLE_MOVE)
    suspend fun postJobSingleMove(
        @Body requestBody: PostJobSingleMoveReqModel
    ): PostJobSingleMoveResModel

    @POST(UserApiEndPoints.POST_JOB_BLUE_COLLAR)
    suspend fun postJobBlueCollar(
        @Body requestBody: PostJobBlueCollarReqModel
    ): PostJobBlueCollarResModel

    @POST(UserApiEndPoints.POST_JOB_MULTI_MOVE)
    suspend fun postJobMultiMove(
        @Body requestBody: PostJobMultiMoveReqModel
    ): PostJobMultiMoveResModel

    @POST(UserApiEndPoints.USER_JOB_POST_DETAILS)
    suspend fun userJobPostDetails(
        @Body requestBody: MyJobPostReqModel
    ): MyJobPostResModel

    @POST(UserApiEndPoints.USER_JOB_POST_VIEW_DETAILS)
    suspend fun userJobPostViewDetails(
        @Body requestBody: MyJobPostViewReqModel
    ): MyJobPostViewResModel

    @POST(UserApiEndPoints.VIEW_BIDS)
    suspend fun viewBids(
        @Body requestBody: ViewBidsReqModel
    ): ViewBidsResModel

    @POST(UserApiEndPoints.VIEW_PROPOSAL)
    suspend fun viewProposal(
        @Body requestBody: ViewProposalReqModel
    ): ViewProposalResModel

    @POST(UserApiEndPoints.POST_DISCUSSION)
    suspend fun postDiscussion(
        @Body requestBody: DiscussionBoardMessageReqModel
    ): DiscussionBoardMessageResModel

    @POST(UserApiEndPoints.DISCUSSION_LIST)
    suspend fun postDiscussionList(
        @Body requestBody: DiscussionListReqModel
    ): DiscussionListResModel

    @GET(UserApiEndPoints.SET_GOALS)
    suspend fun setGoals(
        @Query("key") key: String
    ): SetGoalsResModel

    @POST(UserApiEndPoints.SAVE_INSTALLMENTS)
    suspend fun saveInstallment(
        @Body requestBody: SaveInstallmentReqModel
    ): SaveInstallmentResModel

    @POST(UserApiEndPoints.INSTALLMENT_PAYMENT)
    suspend fun installmentPayment(
        @Body requestBody: InstallmentPaymentReqModel
    ): InstallmentPaymentResModel

    @GET(UserApiEndPoints.COMPLAINT_MODULES)
    suspend fun getComplaintModules(
        @Query("key") key: String
    ): ComplaintModuleResModel

    @POST(UserApiEndPoints.POST_FEEDBACK)
    suspend fun postFeedBack(
        @Body requestBody: FeedbackReqModel
    ): FeedbackResModel

    @POST(UserApiEndPoints.POST_COMPLAINT)
    suspend fun postComplaint(
        @Body requestBody: ComplaintReqModel
    ): ComplaintResModel

    @POST(UserApiEndPoints.DELETE_ATTACHMENTS)
    suspend fun deleteAttachment(
        @Body requestBody: AttachmentDeleteReqModel
    ): AttachmentDeleteResModel

    @POST(UserApiEndPoints.LIKE_POST_DISCUSSION)
    suspend fun likePostDiscussion(
        @Body requestBody: LikePostDescussionReqModel
    ): LikePostDiscussionResModel

    @POST(UserApiEndPoints.UPDATE_SINGLE_MOVE_MY_POST_JOB)
    suspend fun updateMyPostJobSingleMove(
        @Body requestBody: MyJobPostSingleMoveEditReqModel
    ): MyPostJobEditResModel

    @POST(UserApiEndPoints.UPDATE_BLUE_COLLAR_MY_POST_JOB)
    suspend fun updateMyPostJobBlueCollar(
        @Body requestBody: MyJobPostEditBlueCollarReqModel
    ): MyPostJobEditResModel

    @POST(UserApiEndPoints.UPDATE_MULTI_MOVE_MY_POST_JOB)
    suspend fun updateMyPostJobMultiMove(
        @Body requestBody: MyJobPostMultiMoveEditReqModel
    ): MyPostJobEditResModel

    @POST(UserApiEndPoints.OFFERS_LIST)
    suspend fun getOffersList(
        @Body requestBody: OffersListReqModel
    ): OffersListResModel

    @GET(UserApiEndPoints.BOOKING_STATUS_OTP)
    suspend fun getBookingStatusOTP(
        @Query("key") key: String,
        @Query("booking_id") bookingId: Int,
        @Query("user_type") userType: String
    ): ResponseBody

    @GET(UserApiEndPoints.VALIDATE_OTP)
    suspend fun getBookingValidateOTP(
        @Query("key") key: String,
        @Query("booking_id") bookingId: Int,
        @Query("sp_id") spId: Int
    ): ResponseBody

    @POST(UserApiEndPoints.POST_JOB_STATUS)
    suspend fun rejectJobPostStatus(
        @Body requestBody: RejectJobPostStatusReqModel
    ): ResponseBody

    @GET(UserApiEndPoints.MY_ACCOUNT_DETAILS)
    suspend fun getMyAccountDetails(
        @Query("key") key: String,
        @Query("users_id") userId: Int
    ): MyAccountDetailsResModel

    @POST(UserApiEndPoints.FUND_TRANSFER)
    suspend fun fundTransfer(
        @Body requestBody: FundTransferReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.WITHDRAW_FUNDS)
    suspend fun withdrawFunds(
        @Body requestBody: WithdrawFundsReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.ALL_BANK_ACCOUNTS)
    suspend fun allBankDetails(
        @Body requestBody: AllBankDetailsReqModel
    ): AllBankDetailsResModel

    @POST(UserApiEndPoints.ADD_BANK_ACCOUNT)
    suspend fun addBankAccount(
        @Body requestBody: AddBankAccountReqModel
    ): ResponseBody

    @GET(UserApiEndPoints.SP_SLOTS)
    suspend fun getSpSlots(
        @Query("key") key: String,
        @Query("sp_id") spId: Int
    ): ResponseBody

    @POST(UserApiEndPoints.RESCHEDULE_BOOKING)
    suspend fun rescheduleBooking(
        @Body requestBody: RescheduleBookingReqModel
    ): RescheduleBookingResModel

    @POST(UserApiEndPoints.CANCEL_BOOKING)
    suspend fun cancelBooking(
        @Body requestBody: UserBookingCancelReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.UPDATE_EXTRA_DEMAND_STATUS)
    suspend fun changeExtraDemandStatus(
        @Body requestBody: ChangeExtraDemandStatusReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.POST_APPROVE_REJECT_INSTALLMENTS)
    suspend fun postInstallmentApproveReject(
        @Body requestBody: PostApproveRejectReqModel
    ): PostApproveRejectResModel

    @GET(UserApiEndPoints.GOALS_INSTALLMENTS_REQUESTS_LIST)
    suspend fun getInstallmentsRequestList(
        @Query("key") key: String,
        @Query("post_job_id") postJobId: Int
    ): GoalsInstallmentsResModel

    @GET(UserApiEndPoints.GET_BOOKING_STATUS_LIST)
    suspend fun getBookingStatusList(
        @Query("key") key: String,
        @Query("booking_id") bookingId: Int
    ): GetBookingStatusListResModel

    @POST(UserApiEndPoints.COMPLAINT_REQUESTS)
    suspend fun complaintsRequests(
        @Body requestBody: MyJobPostReqModel
    ): ComplaintRequestResModel

    @POST(UserApiEndPoints.UPDATE_ALERTS_TO_READ)
    suspend fun updateAlertToRead(
        @Body requestBody: UserUpdateAlertsToReadReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.UPDATE_RESCHEDULE_REQUEST_STATUS)
    suspend fun updateRescheduleStatus(
        @Body requestBody: RescheduleStatusChangeReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.LOGOUT)
    suspend fun logout(
        @Body requestBody: LogoutReqModel
    ): ResponseBody

    @POST(UserApiEndPoints.COMPLETE_BOOKING)
    suspend fun completeBooking(
        @Body requestBody: CompleteBookingReqModel
    ): ResponseBody

    @GET(UserApiEndPoints.CITIES)
    suspend fun getCities(
        @Query("key") key: String,
        @Query("user_id") userId: String,
    ): CitiesResModel

    @POST(UserApiEndPoints.SAVE_USER_UPI)
    suspend fun saveUserUpi(
        @Body requestBody: SaveUserUpiReqModel
    ): SaveUserUpiResModel

    @POST(UserApiEndPoints.GET_USER_UPI)
    suspend fun getUserUpi(
        @Body requestBody: GetUserUpiReqModel
    ): GetUserUpiResModel

    @POST(UserApiEndPoints.SEND_FCM)
    suspend fun sendFcm(
        @Body requestBody: SendFCMReqModel
    ): SendFCMResModel

    @POST(UserApiEndPoints.SEND_FCM_TO_ALL)
    suspend fun sendFcmToAll(
        @Body requestBody: SendFcmToAllReqModel
    ): SendFcmToAllResModel

    @POST(UserApiEndPoints.ADD_FUNDS)
    suspend fun addFunds(
        @Body requestBody: AddFundsReqModel
    ): AddFundsResModel

    @POST(UserApiEndPoints.POST_COMPLAINT)
    suspend fun postComplaints(
        @Body requestBody: RaiseTicketReqModel
    ): RaiseTicketResModel

    @POST(UserApiEndPoints.GET_TXN)
    suspend fun getTxn(
        @Body requestBody: GetTxnReqModel
    ): GetTxnResModel

}